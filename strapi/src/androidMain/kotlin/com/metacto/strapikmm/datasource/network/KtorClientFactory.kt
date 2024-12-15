package com.metacto.strapikmm.datasource.network

import com.metacto.strapikmm.errorhandling.SerializableNetworkError
import com.metacto.strapikmm.sharedpreference.KmmPreference
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.reflect.KClass

actual class KtorClientFactory actual constructor(
    networkLogLevel: NetworkLogLevel,
    shouldShowActualErrorMessages: Boolean,
    val preference: KmmPreference,
) {

    init {
        NetworkLogConfiguration.logLevel = networkLogLevel
        NetworkLogConfiguration.shouldShowActualErrorMessages = shouldShowActualErrorMessages
    }

    actual fun <T : SerializableNetworkError> build(
        errorClass: KClass<T>
    ): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true
            install(ContentNegotiation) {
                json()
            }

            install(DefaultRequest) {
                handleAuthenticationHeader(preference)
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 180_000 // 180 seconds
                connectTimeoutMillis = 180_000 // 180 seconds
                socketTimeoutMillis = 180_000  // 180 seconds
            }

            HttpResponseValidator {

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


                handleResponseExceptionWithRequest { cause, _ ->
                    cause.handleNetworkException<T>(errorClass)
                }
            }
        }
    }
}