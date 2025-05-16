package com.metacto.strapikmm.analytics

import android.content.Context
import com.appsflyer.AppsFlyerLib

actual class AppsFlyerAnalyticsService actual constructor(
    private val context: Any?,
    private val devApiKey: String,
    private val appleAppId: String?,
    private val initializeByDefault: Boolean
) : AnalyticsService {
    actual override val platform: AnalyticsPlatform
        get() = AnalyticsPlatform.APPSFLYER

    init {
        if (context == null || context !is Context) {
            throw IllegalArgumentException("Context must be an Android Context")
        }

        initialize()
    }

    actual override fun initialize() {
        if (!initializeByDefault) {
            return
        }
        AppsFlyerLib.getInstance().init(devApiKey, null, context as Context)
        AppsFlyerLib.getInstance().start(context)
    }

    actual override fun identifyUser(
        userId: String,
        email: String?,
        phone: String?,
        extraProperties: Map<String, Any>
    ) {
        AppsFlyerLib.getInstance().setCustomerUserId(userId)
    }

    actual override fun logout() {
        AppsFlyerLib.getInstance().setCustomerUserId(null)
    }

    actual override fun track(event: String, properties: Map<String, Any>) {
        AppsFlyerLib.getInstance().logEvent(
            context as Context,
            event,
            properties.toMap()
        )
    }
}