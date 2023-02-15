@file:OptIn(ExperimentalJsExport::class)

package com.swensonhe.strapikmm.datasource.network

import com.swensonhe.strapikmm.constants.SharedConstants
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.Logger
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.util.*
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import io.ktor.util.*

@JsExport
enum class NetworkLogLevel {
    NONE,
    REQUEST,
    ALL
}

expect class KtorClientFactory(networkLogLevel: NetworkLogLevel, preference: KmmPreference) {

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