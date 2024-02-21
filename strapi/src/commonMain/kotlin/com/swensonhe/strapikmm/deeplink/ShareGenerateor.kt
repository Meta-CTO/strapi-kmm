package com.swensonhe.strapikmm.deeplink

import com.swensonhe.strapikmm.deeplink.model.BaseUrl

expect object ShareLinkGenerator {
    suspend fun generateShareLink(
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
    ): String?
}