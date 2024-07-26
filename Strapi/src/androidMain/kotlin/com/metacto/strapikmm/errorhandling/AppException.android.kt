package com.metacto.strapikmm.errorhandling

actual open class AppException(
    val errorCode: Int,
    val errorMessage: String,
    val errorBody: String? = null,
    val throwable: Throwable? = null
) : Throwable(errorMessage)