package com.metacto.strapikmm.errorhandling.errortype

import com.rickclephas.kmp.nserrorkt.asNSError
import io.ktor.client.engine.darwin.DarwinHttpRequestException
import platform.CFNetwork.kCFURLErrorCannotConnectToHost
import platform.CFNetwork.kCFURLErrorNotConnectedToInternet

actual fun Throwable.isNetworkException(): Boolean {
    return if (this is DarwinHttpRequestException) {
        this.origin.code.toInt() == kCFURLErrorCannotConnectToHost
                || this.origin.code.toInt() == kCFURLErrorNotConnectedToInternet
    } else {
        isInternetConnectionError(this.toString())
                || this.asNSError().code.toInt() == kCFURLErrorCannotConnectToHost
                || this.asNSError().code.toInt() == kCFURLErrorNotConnectedToInternet
    }
}

fun isInternetConnectionError(errorMessage: String): Boolean {
    val codeRegex = "Code=(\\S+)".toRegex()
    val matchResult = codeRegex.find(errorMessage)
    val codeValue = matchResult?.groups?.get(1)?.value
    return codeValue?.toIntOrNull() == kCFURLErrorCannotConnectToHost
            || codeValue?.toIntOrNull() == kCFURLErrorNotConnectedToInternet
}