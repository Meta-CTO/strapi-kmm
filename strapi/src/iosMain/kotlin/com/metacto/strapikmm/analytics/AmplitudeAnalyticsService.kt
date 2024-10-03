package com.metacto.strapikmm.analytics

import cocoapods.Amplitude.Amplitude

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class AmplitudeAnalyticsService actual constructor(
    private val context: Any?,
    private val apiKey: String
) : AnalyticsService {
    actual override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.AMPLITUDE

    init {
        initialize()
    }

    actual override fun initialize() {
        Amplitude.instance().initializeApiKey(apiKey)
    }

    actual override fun identifyUser(
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

    actual override fun logout() {
        Amplitude.instance().apply {
            uploadEvents()
            clearUserProperties()
            setUserId(null)
        }
    }

    actual override fun track(event: String, properties: Map<String, Any>) {
        // To avoid casting issues, we copy the properties to a mutable map
        val eventProperties = mutableMapOf<Any?, Any>()
        eventProperties.putAll(properties)
        Amplitude.instance().logEvent(event, eventProperties)
    }
}