package com.metacto.strapikmm.analytics

expect class AppsFlyerAnalyticsService(
    context: Any?,
    devApiKey: String,
    appleAppId: String?,
    initializeByDefault: Boolean = true,
) : AnalyticsService {
    override val platform: AnalyticsPlatform
    override fun initialize()
    override fun identifyUser(
        userId: String,
        email: String?,
        phone: String?,
        extraProperties: Map<String, Any>
    )

    override fun logout()
    override fun track(event: String, properties: Map<String, Any>)
}