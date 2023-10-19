package com.swensonhe.strapikmm.datasource.network.extensions

import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.datasource.network.KmmBaseService
import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import com.swensonhe.strapikmm.errorhandling.NetworkError
import com.swensonhe.strapikmm.errorhandling.NetworkErrorMapper
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

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

fun DefaultRequest.DefaultRequestBuilder.handleAuthenticationHeader(preference: KmmPreference) {
    val token = preference.getSecureString(SharedConstants.ACCESS_TOKEN)
    if (token.isNullOrEmpty()
            .not() && (headers[KmmBaseService.IS_AUTHENTICATED] ?: true.toString()).toBooleanStrict()
    ) {
        headers.append(
            SharedConstants.AUTHORIZATION_HEADER,
            "${SharedConstants.BEARER} $token"
        )
    }

    headers.remove(KmmBaseService.IS_AUTHENTICATED)
}