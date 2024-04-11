package com.metacto.strapikmm.errorhandling.errortype

actual fun Throwable.isNetworkException() = this is java.net.UnknownHostException || this is java.net.SocketTimeoutException