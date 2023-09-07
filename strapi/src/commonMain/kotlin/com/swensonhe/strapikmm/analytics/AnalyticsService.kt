package com.swensonhe.strapikmm.analytics
interface AnalyticsService {
    fun initialize()

    fun identifyUser(userId: String, email: String?, phone: String?, extraProperties: Map<String, Any>)

    fun logout()

    fun track(event: String, properties: Map<String, Any> = mapOf())
}