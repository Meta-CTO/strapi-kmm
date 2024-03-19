package com.metaCTO.strapikmm.analytics

interface AnalyticsService {
    val platform: AnalyticsPlatform

    fun initialize()

    fun identifyUser(userId: String, email: String?, phone: String?, extraProperties: Map<String, Any>)

    fun logout()

    fun track(event: String, properties: Map<String, Any> = mapOf())
}