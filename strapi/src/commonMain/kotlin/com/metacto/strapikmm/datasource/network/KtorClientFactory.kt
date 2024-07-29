@file:OptIn(InternalSerializationApi::class)

package com.metacto.strapikmm.datasource.network

import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.metacto.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import com.metacto.strapikmm.errorhandling.NetworkError
import com.metacto.strapikmm.errorhandling.ErrorMapper
import com.metacto.strapikmm.errorhandling.SerializableNetworkError
import com.metacto.strapikmm.sharedpreference.KmmPreference
import com.metacto.strapikmm.util.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.bodyAsText
import io.ktor.util.toMap
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import kotlin.reflect.KClass


expect class KtorClientFactory(
    networkLogLevel: NetworkLogLevel,
    shouldShowActualErrorMessages: Boolean,
    preference: KmmPreference
) {
    fun <T : SerializableNetworkError> build(
        errorClass: KClass<T>
    ): HttpClient
}


fun HttpRequestBuilder.printCURLDescription(
    bodyString: String? = null,
    method: String,
    kmmPreference: KmmPreference
) {
    val url = url
    val urlBuilder = url.buildString()
    Logger("").log("================================================")

    val components = mutableListOf<String>()
    components.add("$ curl -v")
    components.add("-X $method")

    val token = kmmPreference.getSecureString(SharedConstants.ACCESS_TOKEN)
    if (token.isNullOrEmpty().not() && (headers[KmmBaseService.IS_AUTHENTICATED]
            ?: true.toString()).toBooleanStrict()
    ) {
        components.add("-H \"Authorization: Bearer ${token!!.replace("\"", "\\\"")}\"")
    }

    val headersEntries = headers.entries().filter { it.key != KmmBaseService.IS_AUTHENTICATED }

    headersEntries.forEach { entry ->
        entry.value.forEach { value ->
            components.add("-H \"${entry.key}: ${value.replace("\"", "\\\"")}\"")
        }
    }

    if (bodyString != null) {
        components.add("-d \"${bodyString.replace("\\\"", "\\\\\"").replace("\"", "\\\"")}\"")
    }
    components.add("\"$urlBuilder\"")

    val message = components.joinToString(" \\\n\t")
    Logger("").log(message)
    Logger("").log("================================================")
}

fun DefaultRequest.DefaultRequestBuilder.handleAuthenticationHeader(preference: KmmPreference) {
    val token = preference.getSecureString(SharedConstants.ACCESS_TOKEN)
    if (token.isNullOrEmpty()
            .not() && (headers[KmmBaseService.IS_AUTHENTICATED]
            ?: true.toString()).toBooleanStrict()
    ) {
        headers.append(
            SharedConstants.AUTHORIZATION_HEADER,
            "${SharedConstants.BEARER} $token"
        )
    }

    headers.remove(KmmBaseService.IS_AUTHENTICATED)
}

suspend fun <T : SerializableNetworkError> Throwable.handleNetworkException(
    errorClass: KClass<T>
) {
    val isResponseException = this is ResponseException
    val isClientRequestException = this is ClientRequestException
    val isServerResponseException = this is ServerResponseException
    val isRedirectResponseException = this is RedirectResponseException

    val response = if (isResponseException) {
        (this as? ResponseException)?.response
    } else if (isClientRequestException) {
        (this as? ClientRequestException)?.response
    } else if (isServerResponseException) {
        (this as? ServerResponseException)?.response
    } else if (isRedirectResponseException) {
        (this as? RedirectResponseException)?.response
    } else {
        null
    }

    if (response == null) {
        this.handleError()
    } else {
        val bytes = response.body<JsonElement>()
        val errorData =
            JsonFlatter.flat<T>(
                JsonWithIgnoredUnknownKeys.decodeFromJsonElement(bytes),
                errorClass
            )
        val errorResponse =
            JsonWithIgnoredUnknownKeys.decodeFromJsonElement(errorClass.serializer(), errorData)

        val error = ErrorMapper.mapServerError(
            httpErrorCode = errorResponse.httpCode,
            errorCode = errorResponse.code,
            errorMessage = errorResponse.errorMessage,
            errorBody = JsonWithIgnoredUnknownKeys.encodeToString(errorClass.serializer(), errorResponse),
            throwable = this,
            headers = response.headers.toMap()
        )

        throw error
    }
}


inline fun <T : SerializableNetworkError> JsonElement.handleException(
    errorClass: KClass<T>
) {
    val errorData =
        JsonFlatter.flat<T>(JsonWithIgnoredUnknownKeys.decodeFromJsonElement(this), errorClass)
    val errorResponse =
        JsonWithIgnoredUnknownKeys.decodeFromJsonElement(errorClass.serializer(), errorData)

    val error = ErrorMapper.mapServerError(
        httpErrorCode = errorResponse.httpCode,
        errorCode = errorResponse.code,
        errorMessage = errorResponse.errorMessage,
        throwable = Throwable(),
        headers = mapOf()
    )

    throw error
}


suspend fun Throwable.handleError() {
    when (this) {
        is ServerResponseException -> {
            val bodyString = response.bodyAsText()
            Logger("").log("ServerResponseException Error: $bodyString")
            val httpErrorCode = response.status.value
            throw ErrorMapper.mapToAppException(this, bodyString, httpErrorCode)
        }

        is ClientRequestException -> {
            val bodyString = response.bodyAsText()
            Logger("").log("ClientRequestException Error: $bodyString")
            val httpErrorCode = response.status.value
            throw ErrorMapper.mapToAppException(this, bodyString, httpErrorCode)
        }

        is RedirectResponseException -> {
            val bodyString = response.bodyAsText()
            Logger("").log("RedirectResponseException Error: $bodyString")
            val httpErrorCode = response.status.value
            throw ErrorMapper.mapToAppException(this, bodyString, httpErrorCode)
        }

        else -> {
            val className = this::class.simpleName
            val error = this.message ?: this.toString()
            Logger("").log("$className Error: $error")
            throw ErrorMapper.mapToAppException(this, error, -1)
        }
    }
}