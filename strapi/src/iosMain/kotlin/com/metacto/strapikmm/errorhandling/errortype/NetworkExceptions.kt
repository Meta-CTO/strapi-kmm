package com.metacto.strapikmm.errorhandling.errortype

import com.rickclephas.kmp.nserrorkt.asNSError
import io.ktor.client.engine.darwin.DarwinHttpRequestException
import platform.CFNetwork.kCFURLErrorCannotConnectToHost
import platform.CFNetwork.kCFURLErrorNotConnectedToInternet
import platform.CFNetwork.kCFURLErrorDataNotAllowed

private val CONNECTION_ERROR_CODES = listOf(
    kCFURLErrorCannotConnectToHost, kCFURLErrorNotConnectedToInternet, kCFURLErrorDataNotAllowed
)

actual fun Throwable.isNetworkException(): Boolean {
    return if (this is DarwinHttpRequestException) {
        this.origin.code.toInt() in CONNECTION_ERROR_CODES
    } else {
        isInternetConnectionError(this.toString()) || this.asNSError().code.toInt() in CONNECTION_ERROR_CODES
    }
}

fun isInternetConnectionError(errorMessage: String): Boolean {
    val codeRegex = "Code=(\\S+)".toRegex()
    val matchResult = codeRegex.find(errorMessage)
    val codeValue = matchResult?.groups?.get(1)?.value
    return codeValue?.toIntOrNull() in CONNECTION_ERROR_CODES
}