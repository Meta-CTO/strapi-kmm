package com.swensonhe.strapikmm.util

/**
 * Provides information about the build configuration.
 */
actual class BuildConfig {
    /**
     * Checks if the application is running on an Android platform.
     *
     * @return `true` if the application is running on an Android platform, `false` otherwise.
     */
    actual fun isAndroid() = false // false for web
}