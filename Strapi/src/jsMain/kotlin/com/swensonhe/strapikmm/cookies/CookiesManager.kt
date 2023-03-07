package com.swensonhe.strapikmm.cookies

@JsExport
class CookiesManager {
    private val jsCookie = JsManager.getConstructor()

    fun set(name: dynamic, value: dynamic): dynamic {
        return jsCookie.asDynamic().set(name, value)
    }

    fun setWithOptions(name: dynamic, value: dynamic, options: CookieOptions): dynamic {
        return jsCookie.asDynamic().set(name, value, options.toJs())
    }

    fun getWithOptions(name: dynamic, options: CookieOptions): dynamic {
        val value = jsCookie.asDynamic().get(name, options.toJs())
        return if (value == null || value == undefined) {
            null
        } else {
            value
        }
    }

    fun get(name: dynamic): dynamic {
        val value = jsCookie.asDynamic().get(name)
        return if (value == null || value == undefined) {
            null
        } else {
            value
        }
    }

    fun removeWithOptions(name: String, options: CookieOptions): dynamic {
        return jsCookie.asDynamic().remove(name, options.toJs())
    }

    fun remove(name: String): dynamic {
        return jsCookie.asDynamic().remove(name)
    }

    fun removeAll(): dynamic {
        return jsCookie.asDynamic().remove()
    }

    fun hasValue(key: String): Boolean {
        return jsCookie.asDynamic().get(key) != undefined || jsCookie.asDynamic().get(key) != null
    }
}