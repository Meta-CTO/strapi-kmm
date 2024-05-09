package com.metacto.strapikmm.deeplink

import com.metacto.strapikmm.deeplink.model.DeepLinkError
import com.metacto.strapikmm.deeplink.model.DeepLinkResult

interface AppsFlyerOneLinkListener {
    fun onAppAttribution(
        isOrganic: Boolean,
        extras: Map<Any, Any?>?
    )

    fun onDeepLinkingResult(result: DeepLinkResult?)
    fun onDeepLinkingError(error: DeepLinkError)
}