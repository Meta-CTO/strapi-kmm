package com.swensonhe.strapikmm.analytics

class AnalyticsManager private constructor(
    private val amplitudeService: AnalyticsService?,
    private val cleverTapAnalyticsService: AnalyticsService?
) {
    fun setUserProperties(
        id: String,
        email: String?,
        phone: String?,
        extraProperties: Map<String, Any>
    ) {
        amplitudeService?.identifyUser(id, email, phone, extraProperties)
        cleverTapAnalyticsService?.identifyUser(id, email, phone, extraProperties)
    }

    fun logout() {
        amplitudeService?.logout()
        cleverTapAnalyticsService?.logout()
    }

    fun trackEvent(eventName: String) {
        val trackingEvent = TrackingEvent.Builder(eventName)
            .withAmplitude()
            .withCleverTap()
            .build()
        trackEvent(trackingEvent)
    }

    fun trackEvent(eventName: String, eventProperties: Map<String, Any>) {
        val trackingEvent = TrackingEvent.Builder(eventName)
            .addProperties(eventProperties)
            .withAmplitude()
            .withCleverTap()
            .build()
        trackEvent(trackingEvent)
    }

    fun trackEvent(event: TrackingEvent) {
        val eventProperties = event.properties.toMutableMap()
        if (event.platforms.contains(AnalyticsPlatform.AMPLITUDE)) {
            amplitudeService?.track(event.name, eventProperties)
        }

        if (event.platforms.contains(AnalyticsPlatform.CLEVERTAP)) {
            cleverTapAnalyticsService?.track(event.name, eventProperties)
        }
    }

    class Builder(private val context: Any?) {
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
            return AnalyticsManager(amplitudeService, cleverTapAnalyticsService)
        }
    }
}