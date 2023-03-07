package com.swensonhe.strapikmm.cookies

@JsExport
class CookieOptions(
    val expires: Int? = null,
    val path: String? = null,
    val domain: String? = null,
    val secure: Boolean? = null
)