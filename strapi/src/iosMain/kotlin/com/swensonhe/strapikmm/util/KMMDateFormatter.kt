package com.swensonhe.strapikmm.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.dateWithTimeIntervalSince1970

actual object KMMDateFormatter {
    actual fun format(timestamp: Long, format: String): String {
        val timeInSec = timestamp / 1000.0 // Convert from milliseconds to seconds
        val date = NSDate.dateWithTimeIntervalSince1970(timeInSec)
        val dateFormatter = NSDateFormatter().apply {
            setDateFormat(format)
        }

        return dateFormatter.stringFromDate(date)
    }
}