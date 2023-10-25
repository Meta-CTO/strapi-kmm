package com.swensonhe.strapikmm.datasource.network

import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.strapiNetworkLogLevel
import io.ktor.client.request.*
import io.ktor.http.HttpMethod
import io.ktor.util.*

/**
 * Base service class for Kotlin Multiplatform Mobile (KMM) applications, responsible for building HTTP requests
 * using the provided [StrapiRequestBuilder] and handling common request components such as headers, queries, and authentication.
 *
 * @property baseUrl The base URL of the API endpoints.
 * @property kmmPreference An instance of [KmmPreference] for handling preferences in KMM applications.
 *
 * Usage Example:
 * ```kotlin
 * val baseService = KmmBaseService("https://api.example.com", kmmPreference)
 * val requestBuilder = StrapiRequestBuilder()
 *
 * // Build the HTTP request using the base service
 * val request = baseService.buildRequest(requestBuilder, HttpMethod.Get.value)
 * ```
 *
 * In this example, the `KmmBaseService` class is used to build an HTTP request with customizable components like path, query, and headers.
 * The resulting request can be used for various HTTP methods.
 *
 * @see StrapiRequestBuilder
 * @see HttpRequestBuilder
 * @see KmmPreference
 * @see NetworkLogLevel
 */

open class KmmBaseService(private val baseUrl: String, private val kmmPreference: KmmPreference) {
    /**
     * Build an HTTP request using the provided [StrapiRequestBuilder] and HTTP method.
     *
     * @param requestBuilder The [StrapiRequestBuilder] containing request components.
     * @param method The HTTP method (e.g., "GET", "POST", "PUT", "DELETE", "PATCH").
     * @return An [HttpRequestBuilder] representing the built HTTP request.
     *
     * @see StrapiRequestBuilder
     * @see [HttpMethod]
     */
    fun buildRequest(
        requestBuilder: StrapiRequestBuilder,
        method: HttpMethod,
    ): HttpRequestBuilder {
        // Build the request
        val builderData = requestBuilder.build()
        // Get the endpoint URL
        val endpointUrl = builderData.filterIsInstance<RequestContent.Endpoint>().firstOrNull()
        // Get the headers
        val headers = builderData.filterIsInstance<RequestContent.Header>()
        // Get the queries
        val queries = builderData.filterIsInstance<RequestContent.Query>()
        // Get the body
        val body = builderData.filterIsInstance<RequestContent.Body<*>>()
        // Get the authentication configuration
        val authentication = builderData.filterIsInstance<RequestContent.Authentication>()

        // get the body string for logging if available
        var bodyString: String? = null


        val builder = HttpRequestBuilder().apply {
            // check if the endpoint is a full url or not
            if (endpointUrl?.isFullUrl == true) {
                // if it is a full url, use it as is
                url(endpointUrl.url)
            } else {
                // if it is not a full url, append it to the base url
                url(baseUrl + endpointUrl?.url.orEmpty())
            }

            // iterate through the queries and add them to the request
            queries.forEach { param ->
                // add the query parameter
                parameter(param.key, param.value)
            }

            // iterate through the headers and add them to the request
            headers.forEach {
                // add the header
                header(it.key, it.value)
            }

            // iterate through the authentication configurations and add them to the request
            authentication.forEach {
                // add the authentication header
                header(IS_AUTHENTICATED, it.isAuthenticated.toString())
            }

            // set the body if available
            if (body.isNotEmpty()) {
                // get the body string for logging if available
                bodyString = try {
                    body.first().jsonString
                } catch (throwable: Throwable) {
                    "unable to obtain body data"
                }

                // set the body
                this.setBody(body.first().value!!)
            }
        }

        // print the curl description if the log level is not NONE
        if (strapiNetworkLogLevel != NetworkLogLevel.NONE) {
            // print the curl description
            builder.printCURLDescription(bodyString, method.value, kmmPreference)
        }

        // return the request builder
        return builder
    }

    companion object {
        const val IS_AUTHENTICATED = "STRAPI_KMM_IS_AUTHENTICATED"
    }
}