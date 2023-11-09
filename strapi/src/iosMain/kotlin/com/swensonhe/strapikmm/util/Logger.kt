package com.swensonhe.strapikmm.util

import com.swensonhe.strapikmm.datasource.network.NetworkLogLevel

/**
 * A iOS-specific logger used to log messages with optional class name prefix.
 *
 * @param className An optional class name to be used as a prefix in log messages.
 */
actual class Logger actual constructor(
    private val className: String
) {

    /**
     * Logs a message to the console with an optional class name prefix.
     *
     * @param msg The message to log.
     */
    actual fun log(msg: String) {
        // Check if logging is enabled (based on the network log level)
        if (strapiNetworkLogLevel == NetworkLogLevel.NONE) return

        if (className.isEmpty()) {
            // Log the message without a class name prefix
            println(msg)
        } else {
            // Log the message with the specified class name prefix
            println("$className: $msg")
        }
    }
}