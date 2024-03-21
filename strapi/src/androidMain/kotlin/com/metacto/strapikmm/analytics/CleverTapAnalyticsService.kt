package com.metacto.strapikmm.analytics

import android.content.Context
import com.clevertap.android.sdk.CleverTapAPI

actual class CleverTapAnalyticsService actual constructor(
    private val context: Any?
) : AnalyticsService {
    override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.CLEVERTAP

    init {
        if (context == null || context !is Context) {
            throw IllegalArgumentException("Context must be an Android Context")
        }

        initialize()
    }

    override fun initialize() {

    }

    override fun identifyUser(
        userId: String,
        email: String?,
        phone: String?,
        extraProperties: Map<String, Any>
    ) {
        val userProperties = HashMap<String, Any>()
        userProperties["Identity"] = userId
        email?.let { userProperties["Email"] = it }
        phone?.let { userProperties["Phone"] = it }
        userProperties.putAll(extraProperties)

        CleverTapAPI.getDefaultInstance(context as Context)?.apply {
            onUserLogin(userProperties, userId)
            pushProfile(userProperties)
        }
    }

    override fun logout() {
        CleverTapAPI.getDefaultInstance(context as Context)?.apply {
            onUserLogin(hashMapOf(), null)
            pushProfile(hashMapOf())
        }
    }

    override fun track(event: String, properties: Map<String, Any>) {
        CleverTapAPI.getDefaultInstance(context as Context)?.apply {
            pushEvent(event, properties)
        }
    }
}