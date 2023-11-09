package com.swensonhe.strapikmm.analytics

import android.content.Context
import com.clevertap.android.sdk.CleverTapAPI

/**
 * The [CleverTapAnalyticsService] is an implementation of the [AnalyticsService] interface
 * for the CleverTap analytics platform.
 *
 * @param context The Android context or any platform-specific context (actual type may vary).
 */
actual class CleverTapAnalyticsService actual constructor(
    private val context: Any?
) : AnalyticsService {
    /**
     * Set the platform associated with this analytics service (CleverTap).
     */
    override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.CLEVERTAP

    /**
     * Initializes the CleverTap analytics service.
     */
    init {
        // Check if the context is an Android Context (required for CleverTap)
        if (context == null || context !is Context) {
            // Throw an exception if the context is not an Android Context or is null
            throw IllegalArgumentException("Context must be an Android Context")
        }

        // Initialize CleverTap
        initialize()
    }

    override fun initialize() {
        // no-op for CleverTap
    }

    override fun identifyUser(
        userId: String,
        email: String?,
        phone: String?,
        extraProperties: Map<String, Any>
    ) {

        val userProperties = HashMap<String, Any>()
        // Set the user ID
        userProperties["Identity"] = userId
        // Set the user email
        email?.let { userProperties["Email"] = it }
        // Set the user phone number
        phone?.let { userProperties["Phone"] = it }
        userProperties.putAll(extraProperties)

        // Get the default CleverTap instance and set the user profile
        CleverTapAPI.getDefaultInstance(context as Context)?.apply {
            // Set the user profile
            onUserLogin(userProperties, userId)
            // Push the user profile
            pushProfile(userProperties)
        }
    }

    /**
     * Logs the user out of the analytics service.
     */
    override fun logout() {
        // Get the default CleverTap instance and clear the user profile
        CleverTapAPI.getDefaultInstance(context as Context)?.apply {
            // Clear the user profile
            onUserLogin(hashMapOf(), null)
            // Clear all user profile properties
            pushProfile(hashMapOf())
        }
    }

    /**
     * Tracks an event with the provided event name and properties.
     *
     * @param event The name of the event to track.
     * @param properties The properties associated with the event.
     */
    override fun track(event: String, properties: Map<String, Any>) {
        // Get the default CleverTap instance and push the event
        CleverTapAPI.getDefaultInstance(context as Context)?.apply {
            pushEvent(event, properties)
        }
    }
}