package com.swensonhe.strapikmm.analytics

import cocoapods.Amplitude.Amplitude

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

    override fun identifyUser(userId: String, email: String?, phone: String?, extraProperties: Map<String, Any>) {
        val properties = mutableMapOf<Any?, Any>()
        email?.let { properties["email"] = it }
        phone?.let { properties["phone"] = it }
        extraProperties.forEach { (key, value) ->
            properties[key] = value
        }

        Amplitude.instance().apply {
            setUserId(userId)
            setUserProperties(properties)
        }
    }

    override fun logout() {
        Amplitude.instance().apply {
            setUserId(null)
            setUserProperties(mapOf<Any?, Any>())
        }
    }

    override fun track(event: String, properties: Map<String, Any>) {
        // To avoid casting issues, we copy the properties to a mutable map
        val trackingProperties = mutableMapOf<Any?, Any>()
        properties.forEach { (key, value) ->
            trackingProperties[key] = value
        }

        Amplitude.instance().logEvent(event, trackingProperties)
    }
}