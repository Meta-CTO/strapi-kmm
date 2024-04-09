package com.metacto.strapikmm.errorhandling.errortype

import com.rickclephas.kmp.nserrorkt.asNSError

actual fun Throwable.isNetworkException(): Boolean {
    return this.asNSError().domain == "NSURLErrorDomain"
}
