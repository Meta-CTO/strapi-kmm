package com.metaCTO.strapikmm.errorhandling.errortype

import com.metaCTO.strapikmm.errorhandling.AppException

class NoConnectionException(code: Int, errorMessage: String) :
    AppException(errorCode = code, errorMessage = errorMessage)

class TimeOutException(code: Int, errorMessage: String) :
    AppException(errorCode = code, errorMessage = errorMessage)

class UnexpectedException(code: Int, errorMessage: String, throwable: Throwable) :
    AppException(errorCode = code, errorMessage = errorMessage, throwable = throwable)

class UnAuthorizedException(code: Int, errorMessage: String, throwable: Throwable) :
    AppException(errorCode = code, errorMessage = errorMessage, throwable = throwable)
