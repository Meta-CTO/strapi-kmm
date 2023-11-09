package com.swensonhe.strapikmm.analytics

import android.content.Context
import com.amplitude.analytics.connector.util.toJSONObject
import com.amplitude.api.Amplitude


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
        // Check if the context is an Android Context (required for Amplitude)
        if (context == null || context !is Context) {
            // Throw an exception if the context is not an Android Context or is null
            throw IllegalArgumentException("Context must be an Android Context")
        }

        initialize()
    }

    /**
     * Initializes the Amplitude analytics service with the API key.
     */
    override fun initialize() {
        // Initialize Amplitude with the API key
        Amplitude.getInstance().initialize(context as Context, apiKey)

        // Enable foreground tracking
        if (context.applicationContext as? android.app.Application != null) {
            Amplitude.getInstance()
                .enableForegroundTracking(context.applicationContext as android.app.Application)
        }
    }

    /**
     * Identifies the user with the provided user information and extra properties.
     *
     * @param userId The unique identifier for the user.
     * @param email The user's email address.
     * @param phone The user's phone number.
     * @param extraProperties Additional user properties to set.
     */
    override fun identifyUser(userId: String, email: String?, phone: String?, extraProperties: Map<String, Any>) {
        val userProperties = HashMap<String, Any>()
        userProperties["user_id"] = userId
        email?.let { userProperties["email"] = it }
        phone?.let { userProperties["phone"] = it }
        userProperties.putAll(extraProperties)

        // submit user data
        Amplitude.getInstance().apply {
            // set user id
            setUserId(userId)
            // set user properties
            setUserProperties(userProperties.toJSONObject())
        }
    }

    /**
     * Logs out the user from the Amplitude analytics service.
     */
    override fun logout() {

        Amplitude.getInstance().apply {
            // upload events to make sure they are not lost
            uploadEvents()
            // clear user properties
            clearUserProperties()
            // clear user id
            userId = null
        }
    }

    /**
     * Tracks an event with the provided event name and properties.
     *
     * @param event The name of the event to track.
     * @param properties The properties associated with the event.
     */
    override fun track(event: String, properties: Map<String, Any>) {
        // track event with properties
        Amplitude.getInstance().logEvent(event, properties.toJSONObject())
    }
}