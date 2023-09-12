package com.swensonhe.strapikmm.analytics

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

    override fun identifyUser(userId: String, email: String?, phone: String?, extraProperties: Map<String, Any>) {

        val profileUpdate = HashMap<String, Any>()
        profileUpdate["Identity"] = userId
        email?.let { profileUpdate["Email"] = it }
        phone?.let { profileUpdate.put("Phone", it) }
        extraProperties.forEach { (key, value) ->
            profileUpdate[key] = value
        }
        CleverTapAPI.getDefaultInstance(context as Context)?.apply {
            onUserLogin(profileUpdate, userId)
            pushProfile(profileUpdate)
        }
    }

    override fun logout() {
        CleverTapAPI.getDefaultInstance(context as Context)?.apply {
            pushProfile(hashMapOf())
            onUserLogin(hashMapOf(), null)
        }
    }

    override fun track(event: String, properties: Map<String, Any>) {
        CleverTapAPI.getDefaultInstance(context as Context)?.apply {
            pushEvent(event, properties)
        }
    }
}