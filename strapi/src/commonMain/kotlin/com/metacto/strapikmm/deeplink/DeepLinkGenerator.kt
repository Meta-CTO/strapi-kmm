package com.metacto.strapikmm.deeplink

import com.metacto.strapikmm.deeplink.model.BaseUrl

class DeepLinkGenerator private constructor(
    private val context: Any?,
    private val destination: String,
    private val channel: String?,
    private val referrerCustomerId: String?,
    private val referrerName: String?,
    private val referrerID: String?,
    private val campaign: String?,
    private val baseDeepLink: String?,
    private val deepLinkPath: String?,
    private val referrerImageURL: String?,
    private val brandDomain: String?,
    private val baseURL: BaseUrl?,
    private val parameters: Map<String, String>
) {
    class Builder(private val context: Any? = null, private val destination: String) {
        private var channel: String? = null
        private var referrerCustomerId: String? = null
        private var referrerName: String? = null
        private var refererID: String? = null
        private var campaign: String? = null
        private var baseDeepLink: String? = null
        private var deepLinkPath: String? = null
        private var referrerImageURL: String? = null
        private var brandDomain: String? = null
        private var baseURL: BaseUrl? = null
        private val parameters: MutableMap<String, String> = mutableMapOf()

        fun setChannel(channel: String) = apply { this.channel = channel }
        fun setReferrerCustomerId(referrerCustomerId: String) =
            apply { this.referrerCustomerId = referrerCustomerId }
        fun setReferrerName(referrerName: String) = apply { this.referrerName = referrerName }
        fun setReferrerID(referrerID: String) = apply { this.refererID = referrerID }
        fun setCampaign(campaign: String) = apply { this.campaign = campaign }
        fun setBaseDeeplink(baseDeepLink: String) = apply { this.baseDeepLink = baseDeepLink }
        fun setDeeplinkPath(deepLinkPath: String) = apply { this.deepLinkPath = deepLinkPath }
        fun setReferrerImageURL(referrerImageURL: String) =
            apply { this.referrerImageURL = referrerImageURL }
        fun setBrandDomain(brandDomain: String) = apply { this.brandDomain = brandDomain }
        fun setBaseURL(oneLinkID: String, domain: String, appPackage: String) =
            apply { this.baseURL = BaseUrl(oneLinkID, domain, appPackage) }
        fun addParameter(key: String, value: String) = apply { this.parameters[key] = value }
        fun addParameters(params: Map<String, String>) = apply { this.parameters.putAll(params) }
        fun build() = DeepLinkGenerator(
            context,
            destination,
            channel,
            referrerCustomerId,
            referrerName,
            refererID,
            campaign,
            baseDeepLink,
            deepLinkPath,
            referrerImageURL,
            brandDomain,
            baseURL,
            parameters
        )
    }

    suspend fun generateShareLink() = ShareLinkGenerator.generateShareLink(
        context,
        destination,
        channel,
        referrerCustomerId,
        referrerName,
        referrerID,
        campaign,
        baseDeepLink,
        deepLinkPath,
        referrerImageURL,
        brandDomain,
        baseURL,
        parameters
    )
}