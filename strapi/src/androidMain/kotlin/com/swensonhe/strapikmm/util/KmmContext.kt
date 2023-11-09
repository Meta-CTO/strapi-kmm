package com.swensonhe.strapikmm.util

import android.app.Application

/**
 * An alias for the KMM context, which represents a platform-specific context or environment.
 * On different platforms, the actual type of context may vary.
 */
actual typealias KmmContext = Application // Android Application context for Android