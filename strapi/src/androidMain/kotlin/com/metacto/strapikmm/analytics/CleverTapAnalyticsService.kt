package com.metacto.strapikmm.analytics

import android.content.Context
import com.clevertap.android.sdk.CleverTapAPI
import com.metacto.strapikmm.errorhandling.ErrorMapper

actual class CleverTapAnalyticsService actual constructor(
    private val context: Any?,
    private val enableIOSAutoIntegrate: Boolean
) : AnalyticsService {
    actual override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.CLEVERTAP

    init {
        if (context == null || context !is Context) {
            throw ErrorMapper.mapToAppException(
                "Context must be an Android Context",
                -1
            )
        }

        initialize()
    }

    actual override fun initialize() {

    }

    actual override fun identifyUser(
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

    actual override fun logout() {
        CleverTapAPI.getDefaultInstance(context as Context)?.apply {
            onUserLogin(hashMapOf(), null)
            pushProfile(hashMapOf())
        }
    }

    actual override fun track(event: String, properties: Map<String, Any>) {
        CleverTapAPI.getDefaultInstance(context as Context)?.apply {
            pushEvent(event, properties)
        }
    }
}