package com.metacto.strapikmm.errorhandling

actual open class AppException(
    val errorCode: Int,
    val errorMessage: String,
    errorBody: String? = null,
    val throwable: Throwable? = null
) : Throwable(errorMessage)