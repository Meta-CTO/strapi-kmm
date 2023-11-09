package com.swensonhe.strapikmm.util

/**
 * An expect object for date formatting in a Kotlin Multiplatform Mobile (KMM) project.
 *
 * This object defines a platform-specific implementation for formatting a timestamp into a string
 * based on the specified format.
 */
expect object KMMDateFormatter {
    /**
     * Formats a timestamp into a string using the specified format.
     *
     * @param timestamp The timestamp to be formatted.
     * @param format The format string specifying the desired date and time format.
     * @return The formatted date and time string.
     */
    fun format(timestamp: Long, format: String): String
}
