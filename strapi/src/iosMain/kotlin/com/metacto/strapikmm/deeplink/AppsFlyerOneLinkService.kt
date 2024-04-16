@file:OptIn(ExperimentalForeignApi::class)

package com.metacto.strapikmm.deeplink

import cocoapods.AppsFlyerFramework.AFSDKDeepLinkResultStatus
import cocoapods.AppsFlyerFramework.AppsFlyerDeepLinkDelegateProtocol
import cocoapods.AppsFlyerFramework.AppsFlyerDeepLinkResult
import cocoapods.AppsFlyerFramework.AppsFlyerLib
import cocoapods.AppsFlyerFramework.AppsFlyerLibDelegateProtocol
import com.metacto.strapikmm.deeplink.model.DeepLinkError
import com.metacto.strapikmm.deeplink.model.DeepLinkResult
import com.metacto.strapikmm.deeplink.util.getAppAttributionResult
import com.rickclephas.kmp.nserrorkt.asThrowable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import platform.Foundation.NSError
import platform.darwin.NSObject

actual class AppsFlyerOneLinkService actual constructor(
    val options: AppsFlyerOneLinkOptions
) : NSObject(), AppsFlyerDeepLinkDelegateProtocol,  AppsFlyerLibDelegateProtocol
{
    init {
        if (options.appleAppId.isNullOrEmpty()) {
            throw IllegalArgumentException("Apple App ID must be provided for iOS platform")
        }
    }

    actual fun initialize() {
        AppsFlyerLib.shared().apply {
            options.devAppKey.let {
                setAppsFlyerDevKey(options.devAppKey)
            }

            setAppleAppID(options.appleAppId!!)

            options.enableDebugLog?.let {
                setIsDebug(it)
            }

            options.minTimeBetweenSessions?.let {
                setMinTimeBetweenSessions(it.convert())
            }

            options.appInviteOneLinkTemplateId?.let {
                setAppInviteOneLink(it)
            }
        }
    }

    override fun didResolveDeepLink(result: AppsFlyerDeepLinkResult) {
        when (result.status) {
            AFSDKDeepLinkResultStatus.AFSDKDeepLinkResultStatusFound -> {
                val deepLink = result.deepLink
                val deepLinkResult = DeepLinkResult(
                    deepLinkValue = deepLink?.deeplinkValue,
                    campaign = deepLink?.campaign,
                    campaignId = deepLink?.campaignId,
                    clickHttpReferrer = deepLink?.clickHTTPReferrer,
                    isDeferred = deepLink?.isDeferred,
                    mediaSource = deepLink?.mediaSource,
                    matchType = deepLink?.matchType,
                    clickEventJson = deepLink?.clickEvent.toString()
                )

                options.listener.onDeepLinkingResult(deepLinkResult)
            }
            AFSDKDeepLinkResultStatus.AFSDKDeepLinkResultStatusNotFound -> {
                options.listener.onDeepLinkingResult(null)
            }
            AFSDKDeepLinkResultStatus.AFSDKDeepLinkResultStatusFailure -> {
                options.listener.onDeepLinkingError(DeepLinkError(result.error))
            }
            else -> {
                options.listener.onDeepLinkingResult(null)
            }
        }
    }

    override fun onConversionDataFail(error: NSError) {
        // No-op
    }

    override fun onConversionDataSuccess(conversionInfo: Map<Any?, *>) {
        val appConversionResult = conversionInfo.getAppAttributionResult()
        options.listener.onAppAttribution(appConversionResult.isOrganic, appConversionResult.extras)
    }

    actual fun start(
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        AppsFlyerLib.shared().startWithCompletionHandler { dictionary, error ->
            if (error != null) {
                onError.invoke(error.asThrowable())
            } else {
                onSuccess.invoke()
            }
        }
    }

    actual fun start() {
        AppsFlyerLib.shared().start()
    }

    actual fun stop(isStopped: Boolean) {
        AppsFlyerLib.shared().setIsStopped(isStopped)
    }

    actual fun setCustomerUserId(userId: String) {
        AppsFlyerLib.shared().setCustomerUserID(userId)
    }
}