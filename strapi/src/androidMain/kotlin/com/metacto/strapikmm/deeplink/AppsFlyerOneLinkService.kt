package com.metacto.strapikmm.deeplink

import android.content.Context
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.metacto.strapikmm.deeplink.util.getAppAttributionResult
import com.metacto.strapikmm.deeplink.model.toError

actual class AppsFlyerOneLinkService(
    val options: AppsFlyerOneLinkOptions
) {
    private val conversionListener = object : AppsFlyerConversionListener {
        override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
            if (p0 != null) {
                val appConversionResult = p0.getAppAttributionResult()
                options.listener.onAppAttribution(appConversionResult.isOrganic, appConversionResult.extras)
            }
        }

        override fun onConversionDataFail(p0: String?) {
            // No-op
        }

        override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
            if (p0 != null) {
                val appConversionResult = p0.getAppAttributionResult()
                options.listener.onAppAttribution(appConversionResult.isOrganic, appConversionResult.extras)
            }
        }

        override fun onAttributionFailure(p0: String?) {
            // No-op
        }
    }

    private val deepLinkListener = DeepLinkListener { deepLinkResult ->
        when (deepLinkResult.status) {
            DeepLinkResult.Status.FOUND -> {
                val deepLink = deepLinkResult.deepLink
                val result = com.metacto.strapikmm.deeplink.model.DeepLinkResult(
                    deepLinkValue = deepLink.deepLinkValue,
                    campaign = deepLink.campaign,
                    campaignId = deepLink.campaignId,
                    clickHttpReferrer = deepLink.clickHttpReferrer,
                    isDeferred = deepLink.isDeferred,
                    mediaSource = deepLink.mediaSource,
                    matchType = deepLink.matchType,
                    clickEventJson = deepLink.clickEvent.toString()
                )
                options.listener.onDeepLinkingResult(result)
            }

            DeepLinkResult.Status.NOT_FOUND -> {
                options.listener.onDeepLinkingResult(null)
            }

            else -> {
                options.listener.onDeepLinkingError(deepLinkResult.error.toError())
            }
        }
    }
    actual fun initialize() {
        AppsFlyerLib.getInstance().apply {
            options.enableDebugLog?.let { setDebugLog(it) }
            options.minTimeBetweenSessions?.let { setMinTimeBetweenSessions(it) }
            init(options.devAppKey, conversionListener, options.context)
            subscribeForDeepLink(deepLinkListener)
            if (options.appInviteOneLinkTemplateId != null) {
                //set the OneLink template id for share invite links
                setAppInviteOneLink(options.appInviteOneLinkTemplateId)
            }
        }
    }
}

actual data class AppsFlyerOneLinkOptions(
    val context: Context,
    val devAppKey: String,
    val enableDebugLog: Boolean? = null,
    val minTimeBetweenSessions: Int? = null,
    val appInviteOneLinkTemplateId: String? = null,
    val listener: AppsFlyerOneLinkListener
)