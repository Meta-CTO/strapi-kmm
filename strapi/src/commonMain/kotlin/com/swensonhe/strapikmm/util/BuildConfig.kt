package com.swensonhe.strapikmm.util

expect class BuildConfig() {
    fun isAndroid(): Boolean // true is android client, false if iOS
}