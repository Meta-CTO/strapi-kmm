package com.metacto.strapikmm.deeplink.model

import com.metacto.strapikmm.deeplink.AppsFlyerOneLinkService
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

fun AppsFlyerOneLinkService.getDeepLinkValue(values: Map<Any?, *>): String? {
    return values[AppsFlyerConstants.DEEP_LINK_VALUE]?.toString()
}

fun AppsFlyerOneLinkService.getDeepLinkMetadata(deepLinkValue: String): DeepLinkMetadata {
    val values = deepLinkValue.split("__")
    val referrerName =
        values.find { it.startsWith(AppsFlyerConstants.REFERRER_NAME) }?.substringAfter("=")
    val baseDeepLinkPath =
        values.find { it.startsWith(AppsFlyerConstants.BASE_DEEP_LINK) }?.substringAfter("=")
    val channel = values.find { it.startsWith(AppsFlyerConstants.CHANNEL) }?.substringAfter("=")
    val campaign = values.find { it.startsWith(AppsFlyerConstants.CAMPAIGN) }?.substringAfter("=")
    val referrerCustomerId =
        values.find { it.startsWith(AppsFlyerConstants.REFERRER_CUSTOMER_ID) }?.substringAfter("=")
    val referrerUID =
        values.find { it.startsWith(AppsFlyerConstants.REFERRER_UID) }?.substringAfter("=")
    val referrerImageURL =
        values.find { it.startsWith(AppsFlyerConstants.REFERRER_IMAGE_URL) }?.substringAfter("=")
    val extras = values.filter {
        !it.startsWith(AppsFlyerConstants.REFERRER_NAME)
                && !it.startsWith(AppsFlyerConstants.DEEP_LINK_VALUE)
                && !it.startsWith(AppsFlyerConstants.BASE_DEEP_LINK)
                && !it.startsWith(AppsFlyerConstants.CHANNEL)
                && !it.startsWith(AppsFlyerConstants.CAMPAIGN)
                && !it.startsWith(AppsFlyerConstants.REFERRER_CUSTOMER_ID)
                && !it.startsWith(AppsFlyerConstants.REFERRER_UID)
                && !it.startsWith(AppsFlyerConstants.REFERRER_IMAGE_URL)
    }.map {
        it.substringBefore("=") to it.substringAfter("=")
    }

    return DeepLinkMetadata(
        referrerName = referrerName,
        baseDeepLinkPath = baseDeepLinkPath,
        channel = channel,
        campaign = campaign,
        referrerCustomerId = referrerCustomerId,
        referrerUID = referrerUID,
        referrerImageURL = referrerImageURL,
        extras = extras.toMap()
    )
}