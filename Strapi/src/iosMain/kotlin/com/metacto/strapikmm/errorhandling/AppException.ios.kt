package com.metacto.strapikmm.errorhandling

import platform.Foundation.NSError

actual open class AppException(
    val error: NSError
) : Throwable(error.localizedDescription)