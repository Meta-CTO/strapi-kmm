package com.swensonhe.strapikmm.analytics

import android.content.Context
import com.amplitude.analytics.connector.util.toJSONObject
import com.amplitude.api.Amplitude

actual class AmplitudeAnalyticsService actual constructor(
    private val context: Any?,
    private val apiKey: String
) : AnalyticsService {
    init {
        if (context == null || context !is android.content.Context) {
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
        val profileUpdate = HashMap<String, Any>()
        profileUpdate["user_id"] = userId
        email?.let { profileUpdate["email"] = it }
        phone?.let { profileUpdate.put("phone", it) }
        profileUpdate.putAll(extraProperties)

        // submit user data
        Amplitude.getInstance().apply {
            setUserId(userId)
            setUserProperties(profileUpdate.toJSONObject())
        }
    }

    override fun logout() {
        Amplitude.getInstance().apply {
            uploadEvents()
            clearUserProperties()
            userId = null
            regenerateDeviceId()
        }
    }

    override fun track(event: String, properties: Map<String, Any>) {
        Amplitude.getInstance().logEvent(event, properties.toJSONObject())
    }
}