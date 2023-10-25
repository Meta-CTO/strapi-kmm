package com.swensonhe.strapikmm.errorhandling.errortype

import com.swensonhe.strapikmm.errorhandling.AppException

/**
 * An exception class for representing unexpected errors in the application.
 *
 * @param code The error code associated with the exception.
 * @param errorMessage A human-readable error message.
 * @param throwable The optional underlying [Throwable] that caused this exception.
 */
class UnexpectedException(code: Int, errorMessage: String, throwable: Throwable) :
    AppException(errorCode = code, errorMessage = errorMessage, throwable = throwable)

/**
 * An exception class for representing unauthorized access errors in the application.
 *
 * @param code The error code associated with the exception.
 * @param errorMessage A human-readable error message.
 * @param throwable The optional underlying [Throwable] that caused this exception.
 */
class UnAuthorizedException(code: Int, errorMessage: String, throwable: Throwable) :
    AppException(errorCode = code, errorMessage = errorMessage, throwable = throwable)
