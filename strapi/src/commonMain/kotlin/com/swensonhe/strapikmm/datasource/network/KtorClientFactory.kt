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


/**
 * Enumeration representing the level of network logging for JavaScript (JS) applications.
 *
 * @property NONE No network logging is performed.
 * @property REQUEST Log only the details of network requests.
 * @property ALL Log both network requests and responses.
 *
 * Usage Example:
 * ```kotlin
 * val logLevel = NetworkLogLevel.ALL
 * ```
 *
 * In this example, the `NetworkLogLevel` enum is used to specify the level of network logging for KMM applications.
 *
 * */
@JsExport
enum class NetworkLogLevel {
    /** No network logging is performed. */
    NONE,

    /** Log only the details of network requests. */
    REQUEST,

    /** Log both network requests and responses. */
    ALL
}

/**
 * An expect class responsible for creating and configuring an HTTP client instance, used for making network requests.
 *
 * @param networkLogLevel The desired level of network logging.
 * @param preference An instance of [KmmPreference] used for application preferences.
 *
 * Usage Example:
 * ```kotlin
 * val clientFactory = KtorClientFactory(NetworkLogLevel.REQUEST, kmmPreference)
 * val httpClient = clientFactory.build()
 * ```
 *
 * In this example, the `KtorClientFactory` class is used to create an HTTP client instance with a specified network logging level.
 *
 * @see HttpClient
 * @see NetworkLogLevel
 * @see KmmPreference
 */
expect class KtorClientFactory(networkLogLevel: NetworkLogLevel, preference: KmmPreference) {

    /**
     * Build and configure an HTTP client instance.
     *
     * @return An instance of [HttpClient] with the desired configuration.
     */
    fun build(): HttpClient
}

/**
 * Print a cURL command description based on the HTTP request configuration.
 *
 * @param bodyString The optional request body as a string.
 * @param method The HTTP request method (e.g., "GET", "POST", "PUT", "DELETE", "PATCH").
 * @param kmmPreference An instance of [KmmPreference] for accessing application preferences.
 *
 * Usage Example:
 * ```kotlin
 * val requestBuilder = HttpRequestBuilder()
 * requestBuilder.method = HttpMethod.Post
 * requestBuilder.url("https://api.example.com/resource")
 * requestBuilder.header("Content-Type", "application/json")
 *
 * // Add headers, query parameters, and request body as needed
 *
 * // Print a cURL description of the request
 * requestBuilder.printCURLDescription(bodyString, requestBuilder.method.value, kmmPreference)
 * ```
 *
 * This function generates and logs a cURL command description for the provided HTTP request configuration,
 * including the request method, headers, query parameters, and body (if present). It's useful for debugging and testing.
 *
 * @see HttpRequestBuilder
 * @see HttpMethod
 * @see KmmPreference
 */
fun HttpRequestBuilder.printCURLDescription(
    bodyString: String? = null,
    method: String,
    kmmPreference: KmmPreference
) {
    val url = url

    // Build the URL string
    val urlBuilder = url.buildString()

    // Log a separator line to distinguish cURL descriptions
    Logger("").log("================================================")

    val components = mutableListOf<String>()
    components.add("$ curl -v")
    components.add("-X $method")

    val token = kmmPreference.getSecureString(SharedConstants.ACCESS_TOKEN)
    // Include an Authorization header with a bearer token if available
    if (token.isNullOrEmpty().not() && (headers[KmmBaseService.IS_AUTHENTICATED] ?: true.toString()).toBooleanStrict()) {
        components.add("-H \"Authorization: Bearer ${token!!.replace("\"", "\\\"")}\"")
    }

    val headersEntries = headers.entries().filter { it.key != KmmBaseService.IS_AUTHENTICATED }

    // Add other headers to the cURL command
    headersEntries.forEach { entry ->
        entry.value.forEach { value ->
            components.add("-H \"${entry.key}: ${value.replace("\"", "\\\"")}\"")
        }
    }

    // Include the request body in the cURL command, if present
    if (bodyString != null) {
        components.add("-d \"${bodyString.replace("\\\"", "\\\\\"").replace("\"", "\\\"")}\"")
    }

    // Join the cURL command components and log the description
    components.add("\"$urlBuilder\"")

    val message = components.joinToString(" \\\n\t")

    Logger("").log(message)

    // Log another separator line to conclude the cURL description
    Logger("").log("================================================")
}