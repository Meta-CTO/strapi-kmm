package com.swensonhe.strapikmm.analytics

import cocoapods.CleverTap_iOS_SDK.CleverTap

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
     * Sets the platform associated with this analytics service (CleverTap).
     */
    override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.CLEVERTAP

    /**
     * Initializes the CleverTap analytics service.
     */
    init {
        initialize()
    }

    /**
     * Initializes the CleverTap analytics service.
     */
    override fun initialize() {
        CleverTap.autoIntegrate()
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
        userProperties["Identity"] = userId
        email?.let { userProperties["Email"] = it }
        phone?.let { userProperties["Phone"] = it }
        userProperties.putAll(extraProperties)

        CleverTap.sharedInstance()?.apply {
            onUserLogin(userProperties, userId)
            profilePush(userProperties)
        }
    }

    /**
     * Logs out the user from the CleverTap analytics service.
     */
    override fun logout() {
        CleverTap.sharedInstance()?.apply {
            onUserLogin(mapOf<Any?, Any>(), "")
            profilePush(mapOf<Any?, Any>())
        }
    }

    /**
     * Tracks an event with the provided event name and properties.
     *
     * @param event The name of the event to track.
     * @param properties The properties associated with the event.
     */
    override fun track(event: String, properties: Map<String, Any>) {
        // To avoid casting issues, we convert properties to type Any? and Any
        val eventProperties = mutableMapOf<Any?, Any>()
        eventProperties.putAll(properties)

        CleverTap.sharedInstance()?.recordEvent(event, eventProperties)
    }
}
