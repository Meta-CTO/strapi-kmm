package com.metacto.strapikmm.util

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

