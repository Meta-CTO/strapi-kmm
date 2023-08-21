package com.swensonhe.strapikmm.util

expect object KMMDateFormatter {
    fun format(timestamp: Long, format: String): String
}