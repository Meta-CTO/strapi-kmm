package com.swensonhe.strapikmm.analytics

import cocoapods.CleverTap_iOS_SDK.CleverTap

actual class CleverTapAnalyticsService actual constructor(
    private val context: Any?
) : AnalyticsService {
    init {
        initialize()
    }

    override fun initialize() {
        CleverTap.initialize()
    }

    override fun identifyUser(
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

        CleverTap.sharedInstance()?.onUserLogin(userProperties)
    }

    override fun logout() {
        CleverTap.sharedInstance()?.onUserLogin(mapOf<Any?, Any>())
    }

    override fun track(event: String, properties: Map<String, Any>) {
        // To avoid casting issues, we convert to type Any? and Any
        val eventProperties = mutableMapOf<Any?, Any>()
        eventProperties.putAll(properties)
        CleverTap.sharedInstance()?.recordEvent(event, eventProperties)
    }
}