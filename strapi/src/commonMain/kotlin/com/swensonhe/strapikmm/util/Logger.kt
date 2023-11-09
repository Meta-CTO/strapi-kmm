package com.swensonhe.strapikmm.util

import com.swensonhe.strapikmm.datasource.network.NetworkLogLevel

var strapiNetworkLogLevel =  NetworkLogLevel.NONE

/**
 * An expect class for logging messages in a Kotlin Multiplatform Mobile (KMM) project.
 *
 * This class provides a platform-specific implementation for logging messages.
 *
 * @param className The name of the class or component associated with the logger.
 */
expect class Logger(className: String) {
    /**
     * Logs a message using the provided logger implementation.
     *
     * @param msg The message to be logged.
     */
    fun log(msg: String)
}

/**
 * Print a debug log message with the given class name and message.
 *
 * @param className The name of the class associated with the log message.
 * @param message The message to be logged.
 */
fun printLogD(className: String?, message: String) {
    println("$className: $message")
}

/**
 * Print a debug log message without a specific class name.
 *
 * @param message The message to be logged.
 */
fun printLogD(message: String) {
    println(message)
}
