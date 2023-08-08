package com.swensonhe.strapikmm.deeplink

actual object DeepLinkProcessor {
    actual fun process(url: String, onProcessed: (String) -> Unit) {
        // There is no deep link processing on web
        // So we just pass the url to the callback
        onProcessed.invoke(url)
    }
}