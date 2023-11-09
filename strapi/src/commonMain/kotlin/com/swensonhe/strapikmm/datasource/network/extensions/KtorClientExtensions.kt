package com.swensonhe.strapikmm.datasource.network.extensions

import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.KmmBaseService
import com.swensonhe.strapikmm.datasource.network.KtorClientFactory
import com.swensonhe.strapikmm.datasource.network.NetworkLogLevel
import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import com.swensonhe.strapikmm.errorhandling.NetworkError
import com.swensonhe.strapikmm.errorhandling.NetworkErrorMapper
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.CustomTokenHandler
import com.swensonhe.strapikmm.util.Logger
import com.swensonhe.strapikmm.util.strapiNetworkLogLevel
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Handles response validation for HTTP calls. This function validates the HTTP response status code and
 * throws appropriate exceptions for different HTTP status code ranges.
 *
 * @receiver The configuration for HTTP call validation.
 */
fun HttpCallValidator.Config.handleResponseValidation() {
    validateResponse { response: HttpResponse ->
        val statusCode = response.status.value
        when (statusCode) {
            in 300..399 -> throw RedirectResponseException(response, response.bodyAsText())
            in 400..499 -> throw ClientRequestException(response, response.bodyAsText())
            in 500..599 -> throw ServerResponseException(response, response.bodyAsText())
        }

        if (statusCode >= 600) {
            throw ResponseException(response, response.bodyAsText())
        }
    }
}

/**
 * Handles response error handling for HTTP calls. This function processes response exceptions, extracts
 * error information, and throws a mapped network error.
 *
 * @receiver The configuration for HTTP call validation.
 */
fun HttpCallValidator.Config.handleResponseError() {
    handleResponseExceptionWithRequest { cause, _ ->
        val responseException =
            cause as? ResponseException ?: return@handleResponseExceptionWithRequest
        val response = responseException.response
        val bytes = response.body<JsonElement>()
        val errorData =
            JsonFlatter.flat<NetworkError>(JsonWithIgnoredUnknownKeys.decodeFromJsonElement(bytes))
        val errorResponse =
            JsonWithIgnoredUnknownKeys.decodeFromJsonElement<NetworkError>(errorData)
        val error = NetworkErrorMapper().mapServerError(
            httpErrorCode = errorResponse.httpStatusCode,
            errorCode = errorResponse.errorCode,
            errorMessage = errorResponse.message,
            throwable = responseException
        )
        throw error
    }
}

/**
 * Handles adding the authentication header to an HTTP request based on the access token stored in preferences.
 * The access token is retrieved from the preferences and added to the request header as an "Authorization" header
 * with the Bearer token type.
 *
 * @receiver The builder for configuring an HTTP request.
 * @param preference The preference instance used for retrieving the access token.
 */
fun DefaultRequest.DefaultRequestBuilder.handleAuthenticationHeader(preference: KmmPreference) {
    val sharedToken = preference.getSecureString(SharedConstants.ACCESS_TOKEN)
    val customToken = CustomTokenHandler.getToken()

    val finalToken = if (customToken.isNotEmpty()) {
        customToken
    } else if (sharedToken.isNullOrEmpty().not()) {
        sharedToken
    } else {
        null
    }

    if(strapiNetworkLogLevel != NetworkLogLevel.NONE) {
        Logger("Request: ").log("token: $finalToken")
    }

    // Check if a valid token is available and the request should be authenticated.
    if (finalToken.isNullOrEmpty().not() && (headers[KmmBaseService.IS_AUTHENTICATED]
            ?: true.toString()).toBooleanStrict()
    ) {
        headers.remove(KmmBaseService.IS_AUTHENTICATED)

        // Append the access token with the "Bearer" prefix to the request's authorization header.
        headers.append(
            SharedConstants.AUTHORIZATION_HEADER,
            "${SharedConstants.BEARER} $finalToken"
        )
    }
}


/**
 * Builds and configures a Ktor [HttpClient] instance for making HTTP requests.
 *
 * @return An instance of [HttpClient] with the specified configurations.
 */

fun KtorClientFactory.createHttpClient(
    networkLogLevel: NetworkLogLevel,
    preference: KmmPreference,
    platform: HttpClientEngineFactory<*>
): HttpClient {
    strapiNetworkLogLevel = networkLogLevel

    return HttpClient(platform) {
        expectSuccess = true
        install(ContentNegotiation) {
            json()
        }

        // Install a custom DefaultRequest feature for handling authentication headers.
        install(DefaultRequest) {
            handleAuthenticationHeader(preference)
        }

        // Install response validation and error handling.
        HttpResponseValidator {
            handleResponseValidation()
            handleResponseError()
        }
    }
}