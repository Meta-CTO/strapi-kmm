package com.swensonhe.strapikmm.analytics

/**
 * The [CleverTapAnalyticsService] class is an analytics service implementation for CleverTap.
 * It allows tracking user events and data using the CleverTap analytics platform.
 *
 * @param context The android platform specific context or reference required by CleverTap.
 */
expect class CleverTapAnalyticsService(context: Any?) : AnalyticsService
