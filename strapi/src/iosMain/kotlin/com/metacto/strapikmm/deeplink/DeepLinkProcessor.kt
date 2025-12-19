package com.metacto.strapikmm.deeplink

import kotlinx.coroutines.suspendCancellableCoroutine

actual object DeepLinkProcessor {
    @Throws(Throwable::class)
    actual suspend fun process(url: String): String {
        return suspendCancellableCoroutine { cont ->
            // Firebase Dynamic Links removed - returning URL directly
            cont.resumeWith(Result.success(url))
        }
    }
}
