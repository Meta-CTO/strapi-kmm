package com.metacto.strapikmm.deeplink

expect class AppsFlyerOneLinkService(
    options: AppsFlyerOneLinkOptions
) {
    fun initialize()

    fun start(onSuccess: () -> Unit, onError: (Throwable) -> Unit)
    fun start()
    fun stop(isStopped: Boolean)

    fun setCustomerUserId(userId: String)
}

class AppsFlyerOneLinkOptions(
    val context: Any? = null,
    val appleAppId: String? = null,
    val devAppKey: String,
    val enableDebugLog: Boolean? = null,
    val minTimeBetweenSessions: Int? = null,
    val appInviteOneLinkTemplateId: String? = null,
    val listener: AppsFlyerOneLinkListener,
    val oneLinkCustomDomains: List<String>? = null
)