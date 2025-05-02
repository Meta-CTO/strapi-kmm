package com.metacto.strapikmm.analytics

expect class CleverTapAnalyticsService(
    context: Any?,
    enableIOSAutoIntegrate: Boolean
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