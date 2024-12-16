package com.metacto.strapikmm.datasource.network

import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.errorhandling.SerializableNetworkError
import com.metacto.strapikmm.sharedpreference.KmmPreference
import com.metacto.strapikmm.sharedpreference.TokenHandler
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlin.reflect.KClass

actual class KtorClientFactory actual constructor(
    networkLogLevel: NetworkLogLevel,
    shouldShowActualErrorMessages: Boolean,
    val preference: KmmPreference
) {

    init {
        NetworkLogConfiguration.logLevel = networkLogLevel
        NetworkLogConfiguration.shouldShowActualErrorMessages = shouldShowActualErrorMessages
    }

    actual fun <T : SerializableNetworkError> build(
        errorClass: KClass<T>
    ): HttpClient {

        return HttpClient(Js) {
            expectSuccess = true
            install(ContentNegotiation) {
                json()
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 1800_000 // 1800 seconds
                connectTimeoutMillis = 1800_000 // 1800 seconds
                socketTimeoutMillis = 1800_000  // 1800 seconds
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

                if(NetworkLogConfiguration.logLevel != NetworkLogLevel.NONE) {
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
                    // TODO: Handle full token
                    cause.handleNetworkException<T>(errorClass)
                }
            }
        }
    }
}