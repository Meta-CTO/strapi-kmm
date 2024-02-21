package com.swensonhe.strapikmm.deeplink.model

import platform.Foundation.NSError

actual data class DeepLinkError(
    val error: NSError?
)