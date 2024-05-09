package com.metacto.strapikmm.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual object KMMDateFormatter {
    actual fun format(timestamp: Long, format: String): String {
        val date = Date(timestamp)

        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(date)
    }
}