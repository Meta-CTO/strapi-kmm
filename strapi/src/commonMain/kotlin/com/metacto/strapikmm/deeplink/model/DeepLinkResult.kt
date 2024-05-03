package com.metacto.strapikmm.deeplink.model


sealed class DeepLinkResult {
    data class Found(
        val deepLinkValue: String?,
        val campaign: String?,
        val campaignId: String?,
        val clickHttpReferrer: String?,
        val isDeferred: Boolean?,
        val mediaSource: String?,
        val matchType: String?,
        val clickEventJson: String?,
        val metadata: DeepLinkMetadata?
    ) : DeepLinkResult()

    data object NotFound: DeepLinkResult()
}
