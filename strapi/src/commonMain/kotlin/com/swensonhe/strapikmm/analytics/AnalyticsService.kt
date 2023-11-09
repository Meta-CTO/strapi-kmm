package com.swensonhe.strapikmm.analytics

/**
 * The [AnalyticsService] interface defines a contract for analytics services to track user events and data.
 */
interface AnalyticsService {
    /**
     * Gets the platform associated with this analytics service.
     */
    val platform: AnalyticsPlatform

    /**
     * Initializes the analytics service, preparing it for event tracking.
     */
    fun initialize()

    /**
     * Identifies the user for analytics tracking.
     *
     * @param userId The unique identifier of the user.
     * @param email The user's email address (if available).
     * @param phone The user's phone number (if available).
     * @param extraProperties Additional properties associated with the user.
     */
    fun identifyUser(userId: String, email: String?, phone: String?, extraProperties: Map<String, Any>)

    /**
     * Logs the user out from analytics tracking.
     */
    fun logout()

    /**
     * Tracks an event with optional event-specific properties.
     *
     * @param event The name of the event to track.
     * @param properties Additional properties associated with the event (if any).
     */
    fun track(event: String, properties: Map<String, Any> = mapOf())
}
