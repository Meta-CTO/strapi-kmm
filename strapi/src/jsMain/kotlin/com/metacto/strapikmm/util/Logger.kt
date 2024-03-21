package com.metacto.strapikmm.util

import com.metacto.strapikmm.datasource.network.NetworkLogLevel

actual class Logger actual constructor(
    private val className: String
) {

    actual fun log(msg: String) {
        if (strapiNetworkLogLevel == NetworkLogLevel.NONE) return
        if (className.isEmpty()) {
            console.log(msg)
        } else {
            console.log("$className: $msg")
        }
    }
}