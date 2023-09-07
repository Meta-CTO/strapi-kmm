package com.swensonhe.strapikmm.analytics

actual class AmplitudeAnalyticsService actual constructor(
    context: Any?,
    apiKey: String
) : AnalyticsService {
    override fun initialize() {
        // Not needed
    }

    override fun identifyUser(userId: String, email: String?, phone: String?, extraProperties: Map<String, Any>) {
        // Not needed
    }

    override fun logout() {
        // Not needed
    }

    override fun track(event: String, properties: Map<String, Any>) {
        // Not needed
    }
}