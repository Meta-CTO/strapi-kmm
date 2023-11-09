package com.swensonhe.strapikmm.analytics

/**
 *  Provide Amplitude Analytics Service for web
 * We didn't implement any analytics service for Web (For now), so we just return a no-op implementation here.
 * ** Any PRs to implement it for Web are welcome! **
 */
actual class AmplitudeAnalyticsService actual constructor(
    context: Any?,
    apiKey: String
) : AnalyticsService {
    override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.AMPLITUDE

    override fun initialize() {
        // no-op
    }

    override fun identifyUser(userId: String, email: String?, phone: String?, extraProperties: Map<String, Any>) {
        // no-op
    }

    override fun logout() {
        // no-op
    }

    override fun track(event: String, properties: Map<String, Any>) {
        // no-op
    }
}