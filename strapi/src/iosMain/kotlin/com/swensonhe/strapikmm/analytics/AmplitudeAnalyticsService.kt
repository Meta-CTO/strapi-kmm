package com.swensonhe.strapikmm.analytics

import cocoapods.Amplitude.Amplitude

/**
 * The [AmplitudeAnalyticsService] is an implementation of the [AnalyticsService] interface
 * for the Amplitude analytics platform.
 *
 * @param context The Android context or any platform-specific context (actual type may vary).
 * @param apiKey The Amplitude API key for initialization.
 */
actual class AmplitudeAnalyticsService actual constructor(
    private val context: Any?,
    private val apiKey: String
) : AnalyticsService {

    /**
     * Set the platform associated with this analytics service (Amplitude).
     */
    override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.AMPLITUDE

    /**
     * Initializes the Amplitude analytics service with the provided API key.
     */
    init {
        initialize()
    }

    /**
     * Initializes the Amplitude analytics service with the API key.
     */
    override fun initialize() {
        Amplitude.instance().initializeApiKey(apiKey)
    }

    /**
     * Identifies the user with the provided user information and extra properties.
     *
     * @param userId The unique identifier for the user.
     * @param email The user's email address.
     * @param phone The user's phone number.
     * @param extraProperties Additional user properties to set.
     */
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

    /**
     * Logs out the user from the Amplitude analytics service.
     */
    override fun logout() {
        Amplitude.instance().apply {
            uploadEvents()
            clearUserProperties()
            setUserId(null)
        }
    }

    /**
     * Tracks an event with the provided event name and properties.
     *
     * @param event The name of the event to track.
     * @param properties The properties associated with the event.
     */
    override fun track(event: String, properties: Map<String, Any>) {
        // To avoid casting issues, we copy the properties to a mutable map
        val eventProperties = mutableMapOf<Any?, Any>()
        eventProperties.putAll(properties)
        Amplitude.instance().logEvent(event, eventProperties)
    }
}
