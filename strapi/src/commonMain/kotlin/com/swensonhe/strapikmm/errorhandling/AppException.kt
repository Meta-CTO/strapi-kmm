package com.swensonhe.strapikmm.errorhandling


/**
 * An open class representing an exception in the application.
 *
 * @param errorCode The error code associated with the exception.
 * @param errorMessage A human-readable error message.
 * @param errorBody An optional error response body or details.
 * @param throwable An optional underlying [Throwable] that caused this exception.
 */
open class AppException(
    val errorCode: Int,
    val errorMessage: String,
    val errorBody: String? = null,
    val throwable: Throwable? = null
) : Throwable(errorMessage)
