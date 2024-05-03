package com.metacto.strapikmm.deeplink.model

import com.metacto.strapikmm.deeplink.util.AppsFlyerConstants

data class DeepLinkMetadata(
    val referrerName: String?,
    val baseDeepLinkPath: String?,
    val channel: String?,
    val campaign: String?,
    val referrerCustomerId: String?,
    val referrerUID: String?,
    val referrerImageURL: String?,
    val extras: Map<Any?, *>?
)

fun Map<Any?, *>.getDeepLinkValue(): String? {
    return this[AppsFlyerConstants.DEEP_LINK_VALUE]?.toString()
}

fun Map<Any?, *>.toDeepLinkMetadata(): DeepLinkMetadata {
    return DeepLinkMetadata(
        referrerName = this[AppsFlyerConstants.REFERRER_NAME]?.toString(),
        baseDeepLinkPath = this[AppsFlyerConstants.BASE_DEEP_LINK_PATH]?.toString(),
        channel = this[AppsFlyerConstants.CHANNEL]?.toString(),
        campaign = this[AppsFlyerConstants.CAMPAIGN]?.toString(),
        referrerCustomerId = this[AppsFlyerConstants.REFERRER_CUSTOMER_ID]?.toString(),
        referrerUID = this[AppsFlyerConstants.REFERRER_UID]?.toString(),
        referrerImageURL = this[AppsFlyerConstants.REFERRER_IMAGE_URL]?.toString(),
        extras = this.getExtras()
    )
}

fun Map<Any?, *>.getExtras(): Map<Any?, *> {
    val extras =
        this.filter {
            it.key != AppsFlyerConstants.REFERRER_NAME
                    && it.key != AppsFlyerConstants.DEEP_LINK_VALUE
                    && it.key != AppsFlyerConstants.BASE_DEEP_LINK_PATH
                    && it.key != AppsFlyerConstants.CHANNEL
                    && it.key != AppsFlyerConstants.CAMPAIGN
                    && it.key != AppsFlyerConstants.REFERRER_CUSTOMER_ID
                    && it.key != AppsFlyerConstants.REFERRER_UID
                    && it.key != AppsFlyerConstants.REFERRER_IMAGE_URL
                    && it.key != AppsFlyerConstants.SCHEME
                    && it.key != AppsFlyerConstants.HOST
                    && it.key != AppsFlyerConstants.LINK
                    && it.key != AppsFlyerConstants.MEDIA_SOURCE
        } as Map<Any?, *>
    return extras
}