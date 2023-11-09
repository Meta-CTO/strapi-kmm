package com.swensonhe.strapikmm.util

import kotlinx.datetime.toJSDate

/**
 * A platform-specific utility for formatting timestamps as strings using a specified date format.
 */
actual object KMMDateFormatter {
    /**
     * Formats a timestamp as a string using the provided date format.
     *
     * @param timestamp The timestamp in milliseconds.
     * @param format The date format to use for formatting.
     * @return A formatted string representation of the timestamp.
     */
    actual fun format(timestamp: Long, format: String): String {
        // Convert the timestamp to a JS date
        val jsDate = timestamp.toInstant().toJSDate()
        // Get the year as a string
        val year = jsDate.getFullYear().toString()
        // Get the month and day as strings, padding with leading zeros if necessary
        val month = "${jsDate.getMonth() + 1}".padStart(2, '0')
        // Get the day as a string, padding with a leading zero if necessary
        val day = "${jsDate.getDate()}".padStart(2, '0')
        // Get the hours and minutes as strings, padding with leading zeros if necessary
        var hours = "${jsDate.getHours()}".padStart(2, '0')
        // Get the minutes as a string, padding with a leading zero if necessary
        val minutes = "${jsDate.getMinutes()}".padStart(2, '0')

        // Get the am/pm string
        var ampm = ""

        // If the format contains "a" and "hh", then we need to convert the hours to 12-hour format
        if (format.contains("a") && format.contains("hh", ignoreCase = true)) {
            // Determine if the time is AM or PM
            val isPM = jsDate.getHours() >= 12
            // Get the am/pm string
            ampm = if (isPM) "PM" else "AM"
            // Convert the hours to 12-hour format and pad with a leading zero if necessary
            hours = "${(hours.toInt() % 12).coerceAtLeast(1)}".padStart(2, '0')
        }

        // Return the formatted string
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