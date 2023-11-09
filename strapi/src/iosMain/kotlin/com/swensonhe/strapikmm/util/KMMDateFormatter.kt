package com.swensonhe.strapikmm.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.dateWithTimeIntervalSince1970

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
        // Convert timestamp from milliseconds to seconds
        val timeInSec = timestamp / 1000.0
        // Create an NSDate from the converted timestamp
        val date = NSDate.dateWithTimeIntervalSince1970(timeInSec)
        // Create an NSDateFormatter with the specified date format
        val dateFormatter = NSDateFormatter().apply {
            setDateFormat(format)
        }

        // Format the date and return it as a string
        return dateFormatter.stringFromDate(date)
    }
}
