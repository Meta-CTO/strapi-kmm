package com.swensonhe.strapikmm.analytics

/**
 * The [AnalyticsManager] class is responsible for managing analytics services in a Kotlin Multiplatform Mobile (KMM) project.
 *
 * It allows you to configure and interact with multiple analytics services, such as Amplitude and CleverTap.
 *
 * @param services A list of analytics services to manage and track events on.
 */
class AnalyticsManager private constructor(
    private val services: MutableList<AnalyticsService> = mutableListOf()
) {
    /**
     * Set user properties for analytics tracking on all configured analytics services.
     *
     * @param id The user's unique identifier.
     * @param email The user's email address (nullable).
     * @param phone The user's phone number (nullable).
     * @param extraProperties Additional user properties as key-value pairs.
     */
    fun setUserProperties(
        id: String,
        email: String?,
        phone: String?,
        extraProperties: Map<String, Any>
    ) {
        services.forEach {
            it.identifyUser(id, email, phone, extraProperties)
        }
    }

    /**
     * Log out the user from all configured analytics services.
     */
    fun logout() {
        services.forEach {
            it.logout()
        }
    }

    /**
     * Track a basic event with a given name on all configured analytics services.
     *
     * @param eventName The name of the event to track.
     */
    fun trackEvent(eventName: String) {
        val trackingEvent = TrackingEvent.Builder(eventName)
            .trackOnAllAnalyticsPlatform()
            .build()

        trackEvent(trackingEvent)
    }

    /**
     * Track an event with a name and additional properties on all configured analytics services.
     *
     * @param eventName The name of the event to track.
     * @param eventProperties Additional properties associated with the event.
     */
    fun trackEvent(eventName: String, eventProperties: Map<String, Any>) {
        val trackingEvent = TrackingEvent.Builder(eventName)
            .addProperties(eventProperties)
            .trackOnAllAnalyticsPlatform()
            .build()

        trackEvent(trackingEvent)
    }

    /**
     * Track a custom event on all configured analytics services.
     *
     * @param event The custom tracking event to send to analytics services.
     */
    fun trackEvent(event: TrackingEvent) {
        // Make a mutable copy of the event properties map
        val eventProperties = event.properties.toMutableMap()
        // Get the platforms to track the event on
        val platforms = event.platforms
        // Filter the list of services to only those that match the platforms
        val matchingServices = services.filter { platforms.contains(it.platform) }
        // Track the event on each matching service
        matchingServices.forEach {
            it.track(event.name, eventProperties)
        }
    }

    /**
     * Register an analytics service with the [AnalyticsManager].
     *
     * @param service The analytics service to register.
     */
    private fun registerService(service: AnalyticsService) {
        services.add(service)
    }

    /**
     * Builder class for creating an [AnalyticsManager] instance with configured analytics services.
     *
     * @param context The platform-specific context or reference required by some analytics services (e.g., Android context).
     */
    class Builder(private val context: Any?) {
        // Create nullable instances of Amplitude analytics services
        private var amplitudeService: AnalyticsService? = null
        // Create nullable instances of cleverTap analytics services
        private var cleverTapAnalyticsService: AnalyticsService? = null

        /**
         * Configure the Amplitude analytics service with an API key.
         *
         * @param amplitudeKey The API key for Amplitude analytics.
         */
        fun setAmplitudeService(amplitudeKey: String) = apply {
            this.amplitudeService = AmplitudeAnalyticsService(context, amplitudeKey)
            this.amplitudeService?.initialize()
        }

        /**
         * Configure the CleverTap analytics service.
         */
        fun setCleverTapAnalyticsService() = apply {
            this.cleverTapAnalyticsService = CleverTapAnalyticsService(context)
            this.cleverTapAnalyticsService?.initialize()
        }

        /**
         * Build the [AnalyticsManager] instance with the configured analytics services.
         *
         * @return The [AnalyticsManager] instance with configured analytics services.
         */
        fun build(): AnalyticsManager {
            // Create an instance of the AnalyticsManager
            val analyticsManager = AnalyticsManager()

            // Register the Amplitude analytics service if it exists
            amplitudeService?.let {
                analyticsManager.registerService(it)
            }

            // Register the CleverTap analytics service if it exists
            cleverTapAnalyticsService?.let {
                analyticsManager.registerService(it)
            }

            return analyticsManager
        }
    }
}
