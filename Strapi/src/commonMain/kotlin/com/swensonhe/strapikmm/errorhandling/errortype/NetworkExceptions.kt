package com.swensonhe.strapikmm.errorhandling.errortype

import com.swensonhe.strapikmm.errorhandling.AppException

class NoConnectionException(code: Int, errorMessage: String) :
    AppException(errorCode = code, errorMessage = errorMessage)

class TimeOutException(code: Int, errorMessage: String) :
    AppException(errorCode = code, errorMessage = errorMessage)

class UnexpectedException(code: Int, errorMessage: String, throwable: Throwable) :
    AppException(errorCode = code, errorMessage = errorMessage, throwable = throwable)

class UnAuthorizedException(code: Int, errorMessage: String, throwable: Throwable) :
    AppException(errorCode = code, errorMessage = errorMessage, throwable = throwable)
