package com.metacto.strapikmm.analytics
import cocoapods.AppsFlyerFramework.AppsFlyerLib

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class AppsFlyerAnalyticsService actual constructor(
    private val context: Any?,
    private val devApiKey: String,
    private val appleAppId: String?,
    private val initializeByDefault: Boolean
) : AnalyticsService {
    actual override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.APPSFLYER

    init {
        initialize()
    }

    actual override fun initialize() {
        if (!initializeByDefault) {
            return
        }
        // Initialize AppsFlyer SDK
        if(appleAppId == null) {
            throw IllegalArgumentException("Apple App ID must not be null")
        }
        AppsFlyerLib.shared().setAppsFlyerDevKey(devApiKey)
        AppsFlyerLib.shared().setAppleAppID(appleAppId)
        AppsFlyerLib.shared().start()
    }

    actual override fun identifyUser(
        userId: String,
        email: String?,
        phone: String?,
        extraProperties: Map<String, Any>
    ) {
        AppsFlyerLib.shared().setCustomerUserID(userId)
    }

    actual override fun logout() {
        AppsFlyerLib.shared().setCustomerUserID(null)
    }

    actual override fun track(event: String, properties: Map<String, Any>) {
        // To avoid casting issues, we convert to type Any? and Any
        val eventProperties = mutableMapOf<Any?, Any>()
        eventProperties.putAll(properties)
        AppsFlyerLib.shared().logEvent(
            event,
            eventProperties
        )
    }
}