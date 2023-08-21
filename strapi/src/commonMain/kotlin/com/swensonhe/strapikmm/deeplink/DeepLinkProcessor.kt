package com.swensonhe.strapikmm.deeplink

expect object DeepLinkProcessor {
    suspend fun process(url: String): String
}