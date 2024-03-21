package com.metacto.strapikmm.datasource.network

import com.metacto.strapikmm.sharedpreference.KmmPreference
import com.metacto.strapikmm.util.strapiNetworkLogLevel
import io.ktor.client.request.*

open class KmmBaseService(private val baseUrl: String, private val kmmPreference: KmmPreference) {
    fun buildRequest(
        requestBuilder: StrapiRequestBuilder,
        method: String,
    ): HttpRequestBuilder {
        val builderData = requestBuilder.build()
        val endpointUrl = builderData.filterIsInstance<RequestContent.Endpoint>().firstOrNull()
        val headers = builderData.filterIsInstance<RequestContent.Header>()
        val queries = builderData.filterIsInstance<RequestContent.Query>()
        val body = builderData.filterIsInstance<RequestContent.Body<*>>()
        val authentication = builderData.filterIsInstance<RequestContent.Authentication>()
        var bodyString: String? = null
        val builder = HttpRequestBuilder().apply {
            if (endpointUrl?.isFullUrl == true) {
                url(endpointUrl.url)
            } else {
                url(baseUrl + endpointUrl?.url.orEmpty())
            }

            queries.forEach { param ->
                parameter(param.key, param.value)
            }

            headers.forEach {
                header(it.key, it.value)
            }

            authentication.forEach {
                header(IS_AUTHENTICATED, it.isAuthenticated.toString())
            }

            if (body.isNotEmpty()) {
                bodyString = try {
                    body.first().jsonString
                } catch (throwable: Throwable) {
                    "unable to obtain body data"
                }
                this.setBody(body.first().value!!)
            }
        }

        if (strapiNetworkLogLevel != NetworkLogLevel.NONE) {
            builder.printCURLDescription(bodyString, method, kmmPreference)
        }
        return builder
    }

    companion object {
        const val IS_AUTHENTICATED = "STRAPI_KMM_IS_AUTHENTICATED"
    }
}