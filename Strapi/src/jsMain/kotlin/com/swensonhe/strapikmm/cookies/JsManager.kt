package com.swensonhe.strapikmm.cookies

@JsModule("js-cookie")
@JsNonModule
external object Cookies

internal val jsManager = JsManager.init()

internal object JsManager {
    fun init() {}

    private val jsCookie = Cookies

    @Suppress("UnsafeCastFromDynamic")
    fun getConstructor(): Any {
        return jsCookie
    }
}