package com.metacto.strapikmm.analytics

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