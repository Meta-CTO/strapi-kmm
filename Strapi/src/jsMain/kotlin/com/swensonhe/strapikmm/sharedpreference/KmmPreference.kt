@file:OptIn(ExperimentalJsExport::class)

package com.swensonhe.strapikmm.sharedpreference

import com.russhwolf.settings.Settings
import com.swensonhe.strapikmm.cookies.Cookies
import com.swensonhe.strapikmm.cookies.CookiesManager

@JsExport
actual class KmmPreference actual constructor(
    private val preferences: Settings?,
    private val encryptedPreferences: Settings?,
    private val useCookies: Boolean
) {
    private val cookiesManager = CookiesManager()
    actual fun putInt(key: String, value: Int) {
        putSecureInt(key, value)
    }

    actual fun getInt(key: String, default: Int): Int {
        return getSecureInt(key, default)
    }

    actual fun putSecureInt(key: String, value: Int) {
        if (useCookies) {
            cookiesManager.set(key, value)
        } else {
            preferences?.putInt(key, value)
        }
    }

    actual fun getSecureInt(key: String, default: Int): Int {
        return if (useCookies) {
            (cookiesManager.get(key) ?: default) as Int
        } else {
            preferences?.getIntOrNull(key) ?: default
        }
    }

    actual fun putString(key: String, value: String) {
        putSecureString(key, value)
    }

    actual fun getString(key: String): String? {
        return getSecureString(key)
    }

    actual fun putSecureString(key: String, value: String) {
        if (useCookies) {
            cookiesManager.set(key, value)
        } else {
            preferences?.putString(key, value)
        }
    }

    actual fun getSecureString(key: String): String? {
        return if (useCookies) {
            cookiesManager.get(key) as String?
        } else {
            preferences?.getStringOrNull(key)
        }
    }

    actual fun putDouble(key: String, value: Double) {
        putSecureDouble(key, value)
    }

    actual fun getDouble(key: String, default: Double): Double {
        return getSecureDouble(key, default)
    }

    actual fun putSecureDouble(key: String, value: Double) {
        if (useCookies) {
            cookiesManager.set(key, value)
        } else {
            preferences?.putDouble(key, value)
        }
    }

    actual fun getSecureDouble(key: String, default: Double): Double {
        return if (useCookies) {
            (cookiesManager.get(key) ?: default) as Double
        } else {
            preferences?.getDoubleOrNull(key) ?: default
        }
    }

    actual fun putFloat(key: String, value: Float) {
        putSecureFloat(key, value)
    }

    actual fun getFloat(key: String, default: Float): Float {
        return getSecureFloat(key, default)
    }

    actual fun putSecureFloat(key: String, value: Float) {
        if (useCookies) {
            cookiesManager.set(key, value)
        } else {
            preferences?.putFloat(key, value)
        }
    }

    actual fun getSecureFloat(key: String, default: Float): Float {
        return if (useCookies) {
            (cookiesManager.get(key) ?: default) as Float
        } else {
            preferences?.getFloatOrNull(key) ?: default
        }
    }

    actual fun putLong(key: String, value: Long) {
        putSecureLong(key, value)
    }

    actual fun getLong(key: String, default: Long): Long {
        return getSecureLong(key, default)
    }

    actual fun putSecureLong(key: String, value: Long) {
        if (useCookies) {
            cookiesManager.set(key, value)
        } else {
            preferences?.putLong(key, value)
        }
    }

    actual fun getSecureLong(key: String, default: Long): Long {
        return if (useCookies) {
            (cookiesManager.get(key) ?: default) as Long
        } else {
            preferences?.getLongOrNull(key) ?: default
        }
    }

    actual fun putBool(key: String, value: Boolean) {
        putSecureBool(key, value)
    }

    actual fun getBool(key: String, default: Boolean): Boolean {
        return getSecureBool(key, default)
    }

    actual fun putSecureBool(key: String, value: Boolean) {
        if (useCookies) {
            cookiesManager.set(key, value)
        } else {
            preferences?.putBoolean(key, value)
        }
    }

    actual fun getSecureBool(key: String, default: Boolean): Boolean {
        return if (useCookies) {
            (cookiesManager.get(key) ?: default) as Boolean
        } else {
            preferences?.getBooleanOrNull(key) ?: default
        }
    }

    actual fun contains(key: String, isSecure: Boolean): Boolean {
        return if (useCookies) {
            cookiesManager.hasValue(key)
        } else {
            preferences?.hasKey(key) ?: false
        }
    }

    actual fun clearAll() {
        clearSecuredValues()
        clearAllUserValues()
    }

    actual fun clearSecuredValues() {
        if (useCookies) {
            cookiesManager.removeAll()
        } else {
            preferences?.clear()
        }
    }

    actual fun clearAllUserValues() {
        clearSecuredValues()
    }

    actual fun clearValue(key: String) {
        clearSecureValue(key)
    }

    actual fun clearSecureValue(key: String) {
        if (useCookies) {
            cookiesManager.remove(key)
        } else {
            preferences?.remove(key)
        }
    }
}