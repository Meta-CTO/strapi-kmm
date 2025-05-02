package com.metacto.strapikmm.analytics

actual class CleverTapAnalyticsService actual constructor(
    context: Any?,
    enableIOSAutoIntegrate: Boolean
) : AnalyticsService {
    actual override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.CLEVERTAP

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