package com.swensonhe.strapikmm.deeplink

import cocoapods.FirebaseDynamicLinks.*
import platform.Foundation.NSURL

actual object DeepLinkProcessor {
    actual fun process(url: String, onProcessed: (String) -> Unit) {
        FIRDynamicLinks.dynamicLinks()
            .handleUniversalLink(NSURL(string = url), completion = { dynamicLink, error ->
                if (error != null) {
                    println("getDynamicLink:onFailure $error")
                } else {
                    val deepLink = dynamicLink?.url
                    deepLink?.absoluteString?.let {
                        onProcessed.invoke(it)
                    }
                }
            })
    }
}
