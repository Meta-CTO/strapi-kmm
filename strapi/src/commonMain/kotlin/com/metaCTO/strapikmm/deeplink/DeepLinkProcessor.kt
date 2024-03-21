package com.metaCTO.strapikmm.deeplink

expect object DeepLinkProcessor {
    @Throws(Throwable::class)
    suspend fun process(url: String): String
}