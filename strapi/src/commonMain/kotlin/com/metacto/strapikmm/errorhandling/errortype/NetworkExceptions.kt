package com.metacto.strapikmm.errorhandling.errortype

import com.metacto.strapikmm.errorhandling.AppException

expect fun Throwable.isNetworkException(): Boolean

class TimeOutException(code: Int, errorMessage: String) :
    AppException(errorCode = code, errorMessage = errorMessage)

class UnexpectedException(code: Int, errorMessage: String, throwable: Throwable) :
    AppException(errorCode = code, errorMessage = errorMessage, throwable = throwable)

class UnAuthorizedException(code: Int, errorMessage: String, throwable: Throwable) :
    AppException(errorCode = code, errorMessage = errorMessage, throwable = throwable)

