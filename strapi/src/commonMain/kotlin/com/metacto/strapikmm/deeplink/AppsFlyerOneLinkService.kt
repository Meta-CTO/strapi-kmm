package com.metacto.strapikmm.deeplink

expect class AppsFlyerOneLinkService(
    options: AppsFlyerOneLinkOptions
) {
    fun initialize()
}

class AppsFlyerOneLinkOptions(
    val context: Any? = null,
    val appleAppId: String? = null,
    val devAppKey: String,
    val enableDebugLog: Boolean? = null,
    val minTimeBetweenSessions: Int? = null,
    val appInviteOneLinkTemplateId: String? = null,
    val listener: AppsFlyerOneLinkListener
)