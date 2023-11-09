package com.swensonhe.strapikmm.datasource.network

import com.swensonhe.strapikmm.datasource.network.extensions.createHttpClient
import com.swensonhe.strapikmm.sharedpreference.KmmPreference
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

/**
 * Factory class for creating an instance of Ktor's [HttpClient].
 *
 * @param networkLogLevel The desired network log level for debugging network requests and responses.
 * @param preference An instance of [KmmPreference] used for handling authentication headers.
 */
actual class KtorClientFactory actual constructor(
    private val networkLogLevel: NetworkLogLevel,
    private val preference: KmmPreference
) {
    /**
     * Builds and configures a Ktor [HttpClient] instance for making HTTP requests.
     *
     * @return An instance of [HttpClient] with the specified configurations.
     */
    actual fun build(): HttpClient = createHttpClient(networkLogLevel, preference, Js)
}