package com.swensonhe.strapikmm.util

actual class Logger actual constructor(
    private val className: String
) {

    actual fun log(msg: String) {
        if (className.isEmpty()) {
            console.log(msg)
        } else {
            console.log("$className: $msg")
        }
    }
}