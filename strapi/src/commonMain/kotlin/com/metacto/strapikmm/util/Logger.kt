package com.metacto.strapikmm.util

import com.metacto.strapikmm.datasource.network.NetworkLogLevel

var strapiNetworkLogLevel =  NetworkLogLevel.NONE

interface LogInterceptor {
    fun intercept(message: String)
}

expect class Logger(className: String) {
    fun log(msg: String)

    companion object {
        val interceptors: ArrayList<LogInterceptor>
        fun setInterceptors(interceptors: List<LogInterceptor>)
    }
}

