package com.metacto.strapikmm.datasource.network

import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.metacto.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import com.metacto.strapikmm.errorhandling.NetworkError
import com.metacto.strapikmm.errorhandling.NetworkErrorMapper
import com.metacto.strapikmm.sharedpreference.KmmPreference
import com.metacto.strapikmm.util.Logger
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.js.ExperimentalJsExport

expect class KtorClientFactory(
    networkLogLevel: NetworkLogLevel,
    shouldShowActualErrorMessages: Boolean,
    preference: KmmPreference
) {
    fun build(): HttpClient
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
    if (token.isNullOrEmpty().not() && (headers[KmmBaseService.IS_AUTHENTICATED] ?: true.toString()).toBooleanStrict()) {
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
            .not() && (headers[KmmBaseService.IS_AUTHENTICATED] ?: true.toString()).toBooleanStrict()
    ) {
        headers.append(
            SharedConstants.AUTHORIZATION_HEADER,
            "${SharedConstants.BEARER} $token"
        )
    }

    headers.remove(KmmBaseService.IS_AUTHENTICATED)
}

suspend fun Throwable.handleNetworkException() {
    val isResponseException = this is ResponseException
    val isClientRequestException = this is ClientRequestException
    val isServerResponseException = this is ServerResponseException
    val isRedirectResponseException = this is RedirectResponseException

    Logger("").log("isResponseException: $isResponseException")
    Logger("").log("isClientRequestResponse: $isClientRequestException")
    Logger("").log("isServerResponseException: $isServerResponseException")
    Logger("").log("isRedirectResponseException: $isRedirectResponseException")

    Logger("").log("cause: $cause")
    Logger("").log("this: $this")
    Logger("").log("condition: ${isResponseException || isClientRequestException || isServerResponseException || isRedirectResponseException}")

    val response = if (isResponseException) {
        Logger("").log("ResponseException: ${(this as? ResponseException)?.response}")
        (this as? ResponseException)?.response
    } else if (isClientRequestException) {
        Logger("").log("ClientRequestException: ${(this as? ClientRequestException)?.response}")
        (this as? ClientRequestException)?.response
    } else if (isServerResponseException) {
        Logger("").log("ServerResponseException: ${(this as? ServerResponseException)?.response}")
        (this as? ServerResponseException)?.response
    } else if (isRedirectResponseException) {
        Logger("").log("RedirectResponseException: ${(this as? RedirectResponseException)?.response}")
        (this as? RedirectResponseException)?.response
    } else {
        null
    }

    Logger("").log("response: $response")

    if (response == null) {
        Logger("").log("response == null, call handleError()")
        this.handleError()
    }

    val bytes = response!!.body<JsonElement>()
    Logger("").log("bytes: $bytes")
    val errorData =
        JsonFlatter.flat<NetworkError>(JsonWithIgnoredUnknownKeys.decodeFromJsonElement(bytes))
    Logger("").log("errorData: $errorData")
    val errorResponse =
        JsonWithIgnoredUnknownKeys.decodeFromJsonElement<NetworkError>(errorData)
    Logger("").log("errorResponse: $errorResponse")

    val error = NetworkErrorMapper.mapServerError(
        httpErrorCode = errorResponse.httpStatusCode,
        errorCode = errorResponse.errorCode,
        errorMessage = errorResponse.message,
        errorBody = JsonWithIgnoredUnknownKeys.encodeToString(errorResponse),
        throwable = this
    )

    Logger("").log("Error: $error")

    throw error
}

suspend fun Throwable.handleError() {
    when (this) {
        is ServerResponseException -> {
            val bodyString = response.bodyAsText()
            Logger("").log("ServerResponseException Error: $bodyString")
            val httpErrorCode = response.status.value
            throw NetworkErrorMapper.mapToAppException(this, bodyString, httpErrorCode)
        }

        is ClientRequestException -> {
            val bodyString = response.bodyAsText()
            Logger("").log("ClientRequestException Error: $bodyString")
            val httpErrorCode = response.status.value
            throw NetworkErrorMapper.mapToAppException(this, bodyString, httpErrorCode)
        }

        is RedirectResponseException -> {
            val bodyString = response.bodyAsText()
            Logger("").log("RedirectResponseException Error: $bodyString")
            val httpErrorCode = response.status.value
            throw NetworkErrorMapper.mapToAppException(this, bodyString, httpErrorCode)
        }

        else -> {
            val className = this::class.simpleName
            val error = this.message ?: this.toString()
            Logger("").log("$className Error: $error")
            throw NetworkErrorMapper.mapToAppException(this, error, -1)
        }
    }
}