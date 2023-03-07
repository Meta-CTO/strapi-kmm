package com.swensonhe.strapikmm.cookies

external class Object

inline fun obj(init: dynamic.() -> Unit): dynamic {
    return (Object()).apply(init)
}

internal fun CookieOptions.toJs(): dynamic {
    return obj {
        if (expires != null) this.expires = expires
        if (path != null) this.path = path
        if (domain != null) this.domain = domain
        if (secure != null) this.secure = secure
    }
}