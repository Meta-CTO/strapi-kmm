package com.metacto.strapikmm.deeplink

import com.metacto.strapikmm.deeplink.model.BaseUrl

expect object ShareLinkGenerator {
    suspend fun generateShareLink(
        context: Any?,
        deepLinkValue: String,
        channel: String? = null,
        referrerCustomerId: String? = null,
        referrerName: String? = null,
        referrerUID: String? = null,
        campaign: String? = null,
        baseDeepLink: String? = null,
        deepLinkPath: String? = null,
        referrerImageURL: String? = null,
        brandDomain: String? = null,
        baseURL: BaseUrl? = null,
        parameters: Map<String, String>? = null,
    ): String?
}