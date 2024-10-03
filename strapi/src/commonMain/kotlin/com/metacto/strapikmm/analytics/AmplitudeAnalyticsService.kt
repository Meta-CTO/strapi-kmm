package com.metacto.strapikmm.analytics

expect class AmplitudeAnalyticsService(context: Any?, apiKey: String) : AnalyticsService {
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