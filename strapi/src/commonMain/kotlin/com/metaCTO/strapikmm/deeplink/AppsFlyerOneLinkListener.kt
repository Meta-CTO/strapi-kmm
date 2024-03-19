package com.metaCTO.strapikmm.deeplink

import com.metaCTO.strapikmm.deeplink.model.DeepLinkError
import com.metaCTO.strapikmm.deeplink.model.DeepLinkResult

interface AppsFlyerOneLinkListener {
    fun onAppAttribution(
        isOrganic: Boolean,
        extras: Map<Any, Any?>?
    )

    fun onDeepLinkingResult(result: DeepLinkResult?)
    fun onDeepLinkingError(error: DeepLinkError)
}