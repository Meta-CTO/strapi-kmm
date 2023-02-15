package com.swensonhe.strapikmm.datasource.network

import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.strapiNetworkLogLevel
import io.ktor.client.request.*
import io.ktor.util.*

open class KmmBaseService(private val baseUrl: String, private val kmmPreference: KmmPreference) {
    fun buildRequest(
        requestBuilder: StrapiRequestBuilder,
        method: String,
    ): HttpRequestBuilder {
        val builderData = requestBuilder.build()
        val urlSuffix = builderData.first
        val headers = builderData.second.filterIsInstance<RequestContent.Header>()
        val queries = builderData.second.filterIsInstance<RequestContent.Query>()
        val body = builderData.second.filterIsInstance<RequestContent.Body<*>>()
        val authentication = builderData.second.filterIsInstance<RequestContent.Authentication>()
        var bodyString: String? = null
        val builder = HttpRequestBuilder().apply {
            url(baseUrl + urlSuffix)

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