package com.swensonhe.strapikmm.deeplink

/**
 * An interface for processing deep links asynchronously.
 */
expect object DeepLinkProcessor {
    /**
     * Processes a deep link and returns the result as a [String].
     *
     * @param url The deep link URL to process.
     * @return A [String] representing the result of processing the deep link.
     */
    suspend fun process(url: String): String
}