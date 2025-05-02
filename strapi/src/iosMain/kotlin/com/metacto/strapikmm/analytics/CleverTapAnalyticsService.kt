package com.metacto.strapikmm.analytics

import cocoapods.CleverTap_iOS_SDK.CleverTap

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class CleverTapAnalyticsService actual constructor(
    private val context: Any?,
    private val enableIOSAutoIntegrate: Boolean
) : AnalyticsService {
    actual override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.CLEVERTAP

    init {
        initialize()
    }

    actual override fun initialize() {
        if (enableIOSAutoIntegrate) {
            CleverTap.autoIntegrate()
        }
    }

    actual override fun identifyUser(
        userId: String,
        email: String?,
        phone: String?,
        extraProperties: Map<String, Any>
    ) {
        val userProperties = mutableMapOf<Any?, Any>()
        userProperties["Identity"] = userId
        email?.let { userProperties["Email"] = it }
        phone?.let { userProperties["Phone"] = it }
        userProperties.putAll(extraProperties)

        CleverTap.sharedInstance()?.apply {
            onUserLogin(userProperties, userId)
            profilePush(userProperties)
        }
    }

    actual override fun logout() {
        CleverTap.sharedInstance()?.apply {
            onUserLogin(mapOf<Any?, Any>(), "")
            profilePush(mapOf<Any?, Any>())
        }
    }

    actual override fun track(event: String, properties: Map<String, Any>) {
        // To avoid casting issues, we convert to type Any? and Any
        val eventProperties = mutableMapOf<Any?, Any>()
        eventProperties.putAll(properties)

        CleverTap.sharedInstance()?.recordEvent(event, eventProperties)
    }
}