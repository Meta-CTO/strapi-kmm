package com.metaCTO.strapikmm.deeplink

import com.appsflyer.share.ShareInviteHelper
import com.metaCTO.strapikmm.deeplink.model.BaseUrl

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
        if (context == null || context !is android.content.Context) {
            throw IllegalArgumentException("Context is required and must be an instance of android.content.Context")
        }
       val generator =  ShareInviteHelper.generateInviteUrl(context).apply {
            channel?.let {
                setChannel(it)
            }
            referrerCustomerId?.let {
                setReferrerCustomerId(it)
            }
            referrerName?.let {
                setReferrerName(it)
            }
            referrerUID?.let {
                setReferrerUID(it)
            }
            campaign?.let {
                setCampaign(it)
            }
            baseDeepLink?.let {
                setBaseDeeplink(it)
            }
            deepLinkPath?.let {
                setDeeplinkPath(it)
            }
            referrerImageURL?.let {
                setReferrerImageURL(it)
            }

           baseURL?.let {
                setBaseURL(it.oneLinkID, it.domain, it.appPackage)
            }

            parameters?.let {
                addParameters(it)
            }

           brandDomain?.let {
                setBrandDomain(it)
            }
            baseURL?.let {
                setBaseURL(it.oneLinkID, it.domain, it.appPackage)
            }
            parameters?.let {
                addParameters(it)
            }
        }
        return generator.generateLink()
    }
}