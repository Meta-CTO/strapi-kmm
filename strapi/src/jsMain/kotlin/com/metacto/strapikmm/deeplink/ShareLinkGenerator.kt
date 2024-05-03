package com.metacto.strapikmm.deeplink

import com.metacto.strapikmm.deeplink.model.BaseUrl

actual object ShareLinkGenerator {
    actual suspend fun generateShareLink(
        context: Any?,
        deepLinkValue: String,
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
        TODO("Not yet implemented")
    }
}