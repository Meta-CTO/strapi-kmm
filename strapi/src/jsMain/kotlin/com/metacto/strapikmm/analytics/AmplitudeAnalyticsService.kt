package com.metacto.strapikmm.analytics

actual class AmplitudeAnalyticsService actual constructor(
    context: Any?,
    apiKey: String
) : AnalyticsService {
    actual override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.AMPLITUDE

    actual override fun initialize() {
        // no-op
    }

    actual override fun identifyUser(userId: String, email: String?, phone: String?, extraProperties: Map<String, Any>) {
        // no-op
    }

    actual override fun logout() {
        // no-op
    }

    actual override fun track(event: String, properties: Map<String, Any>) {
        // no-op
    }
}