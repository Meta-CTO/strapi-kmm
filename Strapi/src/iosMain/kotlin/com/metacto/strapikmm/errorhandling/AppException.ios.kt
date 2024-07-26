package com.metacto.strapikmm.errorhandling

import platform.Foundation.NSError

actual open class AppException(
    val error: NSError,
    val headers: Map<String, List<String>>? = null
) : Throwable(error.localizedDescription)