package com.swensonhe.strapikmm.deeplink

import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * web platform-specific implementation of a deep link processor that processes deep links on iOS.
 */
actual object DeepLinkProcessor {
    /**
     * Processes a deep link and returns the processed result.
     *
     * @param url the deep link to process.
     * @return the processed result.
     */
    actual suspend fun process(url: String) = suspendCancellableCoroutine<String> {
        // There is no deep link processing on web
        // So we just pass the url to the callback
        it.resumeWith(Result.success(url))
    }
}