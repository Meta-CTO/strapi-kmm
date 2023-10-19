package com.swensonhe.strapikmm.datasource.network

import com.swensonhe.strapikmm.datasource.network.extensions.handleAuthenticationHeader
import com.swensonhe.strapikmm.datasource.network.extensions.handleResponseError
import com.swensonhe.strapikmm.datasource.network.extensions.handleResponseValidation
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import com.swensonhe.strapikmm.util.strapiNetworkLogLevel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

actual class KtorClientFactory actual constructor(networkLogLevel: NetworkLogLevel, private val preference: KmmPreference) {

    init {
        strapiNetworkLogLevel = networkLogLevel
    }

    actual fun build(): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true
            install(ContentNegotiation) {
                json()
            }

            install(DefaultRequest) {
                handleAuthenticationHeader(preference)
            }

            HttpResponseValidator {
                handleResponseValidation()
                handleResponseError()
            }
        }
    }
}

