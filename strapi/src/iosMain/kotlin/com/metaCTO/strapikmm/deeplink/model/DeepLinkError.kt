package com.metaCTO.strapikmm.deeplink.model

import platform.Foundation.NSError

actual data class DeepLinkError(
    val error: NSError?
)