package com.metacto.strapikmm.datasource.network

import kotlin.js.JsExport

@JsExport
enum class NetworkLogLevel {
    NONE,
    REQUEST,
    ALL
}

object NetworkLogConfiguration {
    var logLevel = NetworkLogLevel.NONE
    var shouldShowActualErrorMessages = false
}