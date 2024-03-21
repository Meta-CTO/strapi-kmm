package com.metacto.strapikmm.deeplink

import cocoapods.FirebaseDynamicLinks.*
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import kotlin.coroutines.resumeWithException

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual object DeepLinkProcessor {
    @Throws(Throwable::class)
    actual suspend fun process(url: String): String {
        return suspendCancellableCoroutine { cont ->
            FIRDynamicLinks.dynamicLinks()
                .handleUniversalLink(NSURL(string = url), completion = { dynamicLink, error ->
                    if (error != null) {
                        cont.resumeWithException(Throwable("getDynamicLink:onFailure $error"))
                    } else {
                        val deepLink = dynamicLink?.url
                        deepLink?.absoluteString?.let {
                            cont.resumeWith(Result.success(it))
                        } ?: cont.resumeWithException(Throwable("Unable to obtain dynamicLink"))
                    }
                })
        }
    }
}
