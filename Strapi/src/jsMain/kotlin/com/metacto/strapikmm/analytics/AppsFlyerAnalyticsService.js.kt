package com.metacto.strapikmm.analytics

actual class AppsFlyerAnalyticsService actual constructor(
    context: Any?,
    devApiKey: String,
    appleAppId: String?,
    initializeByDefault: Boolean
) : AnalyticsService {
    actual override val platform: AnalyticsPlatform
        get() = TODO("Not yet implemented")

    actual override fun initialize() {
    }

    actual override fun identifyUser(
        userId: String,
        email: String?,
        phone: String?,
        extraProperties: Map<String, Any>
    ) {
    }

    actual override fun logout() {
    }

    actual override fun track(
        event: String,
        properties: Map<String, Any>
    ) {
    }

}