package com.swensonhe.strapikmm.util

import kotlinx.datetime.toJSDate

actual object KMMDateFormatter {
    actual fun format(timestamp: Long, format: String): String {
        val jsDate = timestamp.toInstant().toJSDate()
        val year = jsDate.getFullYear().toString()
        val month = "${jsDate.getMonth() + 1}".padStart(2, '0')
        val day = "${jsDate.getDate()}".padStart(2, '0')
        var hours = "${jsDate.getHours()}".padStart(2, '0')
        val minutes = "${jsDate.getMinutes()}".padStart(2, '0')
        var ampm = ""


        if (format.contains("a") && format.contains("hh", ignoreCase = true)) {
            val isPM = jsDate.getHours() >= 12
            ampm = if (isPM) "PM" else "AM"
            hours = "${(hours.toInt() % 12).coerceAtLeast(1)}".padStart(2, '0')
        }

        return format
            .replace("YYYY", year, true)
            .replace("MM", month)
            .replace("DD", day, true)
            .replace("HH", hours, true)
            .replace("mm", minutes)
            .replace("a", ampm)
            .replace("s", 0.toString(), true)
            .replace("'", "", true)
    }
}