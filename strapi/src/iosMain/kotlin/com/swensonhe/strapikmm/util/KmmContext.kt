package com.swensonhe.strapikmm.util

import platform.darwin.NSObject

/**
 * An alias for the KMM context, which represents a platform-specific context or environment.
 * On different platforms, the actual type of context may vary.
 */
actual typealias KmmContext = NSObject // NSObject for iOS
