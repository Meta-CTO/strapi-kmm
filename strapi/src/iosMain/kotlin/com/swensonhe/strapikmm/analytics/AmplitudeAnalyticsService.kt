package com.swensonhe.strapikmm.analytics

import cocoapods.Amplitude.Amplitude

import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class AmplitudeAnalyticsService actual constructor(
    private val context: Any?,
    private val apiKey: String
) : AnalyticsService {
    override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.AMPLITUDE

    init {
        initialize()
    }

    override fun initialize() {
        Amplitude.instance().initializeApiKey(apiKey)
    }

    override fun identifyUser(
        userId: String,
        email: String?,
        phone: String?,
        extraProperties: Map<String, Any>
    ) {
        val userProperties = mutableMapOf<Any?, Any>()
        email?.let { userProperties["email"] = it }
        phone?.let { userProperties["phone"] = it }
        userProperties.putAll(extraProperties)

        Amplitude.instance().apply {
            setUserId(userId)
            setUserProperties(userProperties)
        }
    }

    override fun logout() {
        Amplitude.instance().apply {
            uploadEvents()
            clearUserProperties()
            setUserId(null)
        }
    }

    override fun track(event: String, properties: Map<String, Any>) {
        // To avoid casting issues, we copy the properties to a mutable map
        val eventProperties = mutableMapOf<Any?, Any>()
        eventProperties.putAll(properties)
        Amplitude.instance().logEvent(event, eventProperties)
    }
}