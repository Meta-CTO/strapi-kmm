package com.metacto.strapikmm.analytics

import android.content.Context
import com.amplitude.analytics.connector.util.toJSONObject
import com.amplitude.api.Amplitude

actual class AmplitudeAnalyticsService actual constructor(
    private val context: Any?,
    private val apiKey: String
) : AnalyticsService {
    override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.AMPLITUDE

    init {
        if (context == null || context !is Context) {
            throw IllegalArgumentException("Context must be an Android Context")
        }

        initialize()
    }

    override fun initialize() {
        Amplitude.getInstance().initialize(context as Context, apiKey)

        if (context.applicationContext as? android.app.Application != null) {
            Amplitude.getInstance()
                .enableForegroundTracking(context.applicationContext as android.app.Application)
        }
    }

    override fun identifyUser(userId: String, email: String?, phone: String?, extraProperties: Map<String, Any>) {
        val userProperties = HashMap<String, Any>()
        userProperties["user_id"] = userId
        email?.let { userProperties["email"] = it }
        phone?.let { userProperties["phone"] = it }
        userProperties.putAll(extraProperties)

        // submit user data
        Amplitude.getInstance().apply {
            setUserId(userId)
            setUserProperties(userProperties.toJSONObject())
        }
    }

    override fun logout() {
        Amplitude.getInstance().apply {
            uploadEvents()
            clearUserProperties()
            userId = null
        }
    }

    override fun track(event: String, properties: Map<String, Any>) {
        Amplitude.getInstance().logEvent(event, properties.toJSONObject())
    }
}