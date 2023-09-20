package com.swensonhe.strapikmm.analytics

actual class CleverTapAnalyticsService actual constructor(
    context: Any?
) : AnalyticsService {
    override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.CLEVERTAP

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