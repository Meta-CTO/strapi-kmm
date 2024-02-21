package com.swensonhe.strapikmm.deeplink

import com.swensonhe.strapikmm.deeplink.model.DeepLinkError
import com.swensonhe.strapikmm.deeplink.model.DeepLinkResult

interface AppsFlyerOneLinkListener {
    fun onAppAttribution(
        isOrganic: Boolean,
        extras: Map<Any, Any?>?
    )

    fun onDeepLinkingResult(result: DeepLinkResult?)
    fun onDeepLinkingError(error: DeepLinkError)
}