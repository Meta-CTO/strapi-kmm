package com.swensonhe.strapikmm.analytics

/**
 * An expect class for providing analytics services through Amplitude in a Kotlin Multiplatform Mobile (KMM) project.
 *
 * This class is used to set up and send analytics events to Amplitude with a specific API key.
 *
 * @param context The Android platform specific context or reference required by Amplitude.
 * @param apiKey The API key used to authenticate and send analytics data to Amplitude.
 */
expect class AmplitudeAnalyticsService(context: Any?, apiKey: String) : AnalyticsService
