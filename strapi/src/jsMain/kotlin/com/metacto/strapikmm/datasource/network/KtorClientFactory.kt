package com.metacto.strapikmm.datasource.network

import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.metacto.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import com.metacto.strapikmm.errorhandling.NetworkError
import com.metacto.strapikmm.errorhandling.NetworkErrorMapper
import com.metacto.strapikmm.sharedpreference.KmmPreference
import com.metacto.strapikmm.sharedpreference.TokenHandler
import com.metacto.strapikmm.util.strapiNetworkLogLevel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

actual class KtorClientFactory actual constructor(
    networkLogLevel: NetworkLogLevel,
    private val preference: KmmPreference
) {

    init {
        strapiNetworkLogLevel = networkLogLevel
    }

    actual fun build(): HttpClient {

        return HttpClient(Js) {
            expectSuccess = true
            install(ContentNegotiation) {
                json()
            }

            install(DefaultRequest) {
                val sharedToken = preference.getSecureString(SharedConstants.ACCESS_TOKEN)
                val token = TokenHandler.token

                val finalToken = if (sharedToken.isNullOrEmpty().not()) {
                    sharedToken
                } else if (token.isNotEmpty()) {
                    token
                } else {
                    null
                }

                if(strapiNetworkLogLevel != NetworkLogLevel.NONE) {
                    console.log("finalToken: $finalToken")
                }

                if (finalToken.isNullOrEmpty().not()) {
                    headers.append(
                        SharedConstants.AUTHORIZATION_HEADER,
                        "${SharedConstants.BEARER} $finalToken"
                    )
                }
            }

            HttpResponseValidator {

                validateResponse { response: HttpResponse ->
                    val statusCode = response.status.value
                    when (statusCode) {
                        in 300..399 -> throw RedirectResponseException(
                            response,
                            response.bodyAsText()
                        )
                        in 400..499 -> throw ClientRequestException(response, response.bodyAsText())
                        in 500..599 -> throw ServerResponseException(
                            response,
                            response.bodyAsText()
                        )
                    }

                    if (statusCode >= 600) {
                        throw ResponseException(response, response.bodyAsText())
                    }
                }

                handleResponseExceptionWithRequest { cause, _ ->
                    val responseException =
                        cause as? ResponseException ?: return@handleResponseExceptionWithRequest
                    val response = responseException.response
                    val bytes = response.body<JsonElement>()
                    val errorData =
                        JsonFlatter.flat<NetworkError>(
                            JsonWithIgnoredUnknownKeys.decodeFromJsonElement(
                                bytes
                            )
                        )
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
        }
    }
}