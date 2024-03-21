package com.metaCTO.strapikmm.deeplink

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
        TODO("Not yet implemented")
    }
}