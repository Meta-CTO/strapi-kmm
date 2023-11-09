package com.swensonhe.strapikmm.deeplink

import cocoapods.FirebaseDynamicLinks.*
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import kotlin.coroutines.resumeWithException

/**
 * iOS platform-specific implementation of a deep link processor that processes deep links on iOS.
 */
actual object DeepLinkProcessor {
    /**
     * Processes the provided deep link URL and returns the result asynchronously.
     *
     * @param url The deep link URL to process.
     * @return A [Result] containing the processed deep link as a [String], or an exception if processing fails.
     * @throws Throwable if there is an error during the deep link processing.
     */
    actual suspend fun process(url: String) = suspendCancellableCoroutine { cont ->
        // Get the dynamic link from the URL.
        FIRDynamicLinks.dynamicLinks()
            .handleUniversalLink(NSURL(string = url), completion = { dynamicLink, error ->
                if (error != null) {
                    // If there is an error, resume the coroutine with an exception.
                    cont.resumeWithException(Throwable("getDynamicLink:onFailure $error"))
                } else {
                    // If there is no error, resume the coroutine with the deep link URL.
                    val deepLink = dynamicLink?.url
                    // If the deep link is not null, resume the coroutine with the deep link URL.
                    deepLink?.absoluteString?.let {
                        cont.resumeWith(Result.success(it))
                    }
                }
            })
    }
}
