package com.metacto.strapikmm.deeplink

import kotlinx.coroutines.suspendCancellableCoroutine

actual object DeepLinkProcessor {
    actual suspend fun process(url: String) = suspendCancellableCoroutine<String> {
        // There is no deep link processing on web
        // So we just pass the url to the callback
        it.resumeWith(Result.success(url))
    }
}