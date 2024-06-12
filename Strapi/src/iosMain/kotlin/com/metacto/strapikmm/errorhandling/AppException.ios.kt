package com.metacto.strapikmm.errorhandling

import platform.Foundation.NSError

actual open class AppException(
    val errorCode: Int,
    val errorMessage: String,
    val errorBody: String? = null,
    val error: NSError
) : Throwable(errorMessage)