package com.swensonhe.strapikmm.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        // Convert timestamp to Date
        val date = Date(timestamp)

        // Create SimpleDateFormat object
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        // Return date formatted as string
        return dateFormat.format(date)
    }
}