package com.metacto.strapikmm.analytics

import com.metacto.strapikmm.constants.SharedConstants
import com.metacto.strapikmm.sharedpreference.KmmPreference

class AnalyticsManager private constructor(
    private val services: MutableList<AnalyticsService> = mutableListOf(),
    val sharedPreference: KmmPreference
) {

    fun setUserProperties(
        id: String,
        email: String?,
        phone: String?,
        extraProperties: Map<String, Any>
    ) {
        val enableAnalyticsTracking = sharedPreference.getBool(SharedConstants.ENABLE_ANALYTICS_TRACKING, true)
        if (!enableAnalyticsTracking) return
        services.forEach {
            it.identifyUser(id, email, phone, extraProperties)
        }
    }

    fun logout() {
        services.forEach {
            it.logout()
        }
    }

    fun trackEvent(eventName: String) {
        val trackingEvent = TrackingEvent.Builder(eventName)
            .trackOnAllAnalyticsPlatform()
            .build()

        trackEvent(trackingEvent)
    }

    fun trackEvent(eventName: String, eventProperties: Map<String, Any>) {
        val trackingEvent = TrackingEvent.Builder(eventName)
            .addProperties(eventProperties)
            .trackOnAllAnalyticsPlatform()
            .build()

        trackEvent(trackingEvent)
    }

    fun trackEvent(event: TrackingEvent) {
        val enableAnalyticsTracking = sharedPreference.getBool(SharedConstants.ENABLE_ANALYTICS_TRACKING, true)
        if (!enableAnalyticsTracking) return

        val eventProperties = event.properties.toMutableMap()
        val platforms = event.platforms
        val matchingServices = services.filter { platforms.contains(it.platform) }
        matchingServices.forEach {
            it.track(event.name, eventProperties)
        }
    }

    private fun registerService(service: AnalyticsService) {
        services.add(service)
    }

    class Builder(private val context: Any?, private val sharedPreference: KmmPreference) {
        private var amplitudeService: AnalyticsService? = null
        private var cleverTapAnalyticsService: AnalyticsService? = null

        fun setAmplitudeService(amplitudeKey: String) = apply {
            this.amplitudeService = AmplitudeAnalyticsService(context, amplitudeKey)
            this.amplitudeService?.initialize()
        }

        fun setCleverTapAnalyticsService() = apply {
            this.cleverTapAnalyticsService = CleverTapAnalyticsService(context)
            this.cleverTapAnalyticsService?.initialize()
        }

        fun build(): AnalyticsManager {
            val analyticsManager = AnalyticsManager(sharedPreference = sharedPreference)

            amplitudeService?.let {
                analyticsManager.registerService(it)
            }

            cleverTapAnalyticsService?.let {
                analyticsManager.registerService(it)
            }

            return analyticsManager
        }
    }
}
