package com.metacto.strapikmm.util

import com.metacto.strapikmm.datasource.network.NetworkLogLevel

actual class Logger actual constructor(
    private val className: String
) {
    actual fun log(msg: String) {
        if (strapiNetworkLogLevel == NetworkLogLevel.NONE) return

        val message = if(className.isEmpty()) msg else "$className: $msg"
        println(message)

        interceptors.forEach {
            it.intercept(message)
        }
    }

    actual companion object {
        actual fun setInterceptors(interceptors: List<LogInterceptor>) {
            this.interceptors.addAll(interceptors)
        }

        actual val interceptors: ArrayList<LogInterceptor> = arrayListOf()
    }
}