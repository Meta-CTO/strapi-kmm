package com.metacto.strapikmm.util

import com.metacto.strapikmm.datasource.network.NetworkLogConfiguration
import com.metacto.strapikmm.datasource.network.NetworkLogLevel

actual class Logger actual constructor(
    private val className: String
) {
    actual fun log(msg: String) {
        if (NetworkLogConfiguration.logLevel == NetworkLogLevel.NONE) return

        val message = if(className.isEmpty()) msg else "$className: $msg"
        console.log(message)

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