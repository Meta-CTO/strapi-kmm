@file:OptIn(ExperimentalForeignApi::class)

package com.swensonhe.strapikmm.deeplink

import cocoapods.AppsFlyerFramework.AppsFlyerLinkGenerator
import cocoapods.AppsFlyerFramework.AppsFlyerShareInviteHelper
import com.swensonhe.strapikmm.deeplink.model.BaseUrl
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual object ShareLinkGenerator {
    actual suspend fun generateShareLink(
        context: Any?,
        channel: String?,
        referrerCustomerId: String?,
        referrerName: String?,
        referrerUID: String?,
        campaign: String?,
        baseDeepLink: String?,
        deepLinkPath: String?,
        referrerImageURL: String?,
        brandDomain: String?,
        baseURL: BaseUrl?,
        parameters: Map<String, String>?
    ): String? {
        return suspendCancellableCoroutine { cont ->
            val configureLinkGenerator: (AppsFlyerLinkGenerator?) -> AppsFlyerLinkGenerator? =
                { linkGenerator ->
                    channel?.let {
                        linkGenerator?.setChannel(it)
                    }

                    referrerCustomerId?.let {
                        linkGenerator?.setReferrerCustomerId(it)
                    }

                    referrerUID?.let {
                        linkGenerator?.setReferrerUID(it)
                    }

                    referrerName?.let {
                        linkGenerator?.setReferrerName(it)
                    }

                    campaign?.let {
                        linkGenerator?.setCampaign(it)
                    }

                    baseDeepLink?.let {
                        linkGenerator?.setBaseDeeplink(it)
                    }

                    deepLinkPath?.let {
                        linkGenerator?.setDeeplinkPath(it)
                    }

                    referrerImageURL?.let {
                        linkGenerator?.setReferrerImageURL(it)
                    }

                    brandDomain?.let {
                        linkGenerator?.setBrandDomain(it)
                    }

                    parameters?.forEach { entry ->
                        linkGenerator?.addParameterValue(forKey = entry.key, value = entry.value)
                    }

                    linkGenerator
                }

            AppsFlyerShareInviteHelper.generateInviteUrlWithLinkGenerator(configureLinkGenerator) { url ->
                cont.resume(url?.absoluteString)
            }
        }
    }
}