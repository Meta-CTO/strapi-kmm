package com.swensonhe.strapikmm.deeplink

expect object DeepLinkProcessor {
    fun process(url: String, onProcessed: (String) -> Unit)
}