package com.swensonhe.strapikmm.sharedpreference

import com.russhwolf.settings.Settings

actual class KmmPreference actual constructor(
    private val preferences: Settings?,
    private val encryptedPreferences: Settings?,
    private val useCookies: Boolean
) {
    actual fun putInt(key: String, value: Int) {
        preferences?.putInt(key, value)
    }

    actual fun getInt(key: String, default: Int): Int {
        return preferences?.getIntOrNull(key) ?: default
    }

    actual fun putSecureInt(key: String, value: Int) {
        encryptedPreferences?.putInt(key, value)
    }

    actual fun getSecureInt(key: String, default: Int): Int {
        return encryptedPreferences?.getIntOrNull(key) ?: default
    }

    actual fun putString(key: String, value: String) {
        preferences?.putString(key, value)
    }

    actual fun getString(key: String): String? {
        return preferences?.getStringOrNull(key)
    }

    actual fun putSecureString(key: String, value: String) {
        encryptedPreferences?.putString(key, value)
    }

    actual fun getSecureString(key: String): String? {
        return encryptedPreferences?.getStringOrNull(key)
    }

    actual fun putDouble(key: String, value: Double) {
        preferences?.putDouble(key, value)
    }

    actual fun getDouble(key: String, default: Double): Double {
        return preferences?.getDoubleOrNull(key) ?: default
    }

    actual fun putSecureDouble(key: String, value: Double) {
        encryptedPreferences?.putDouble(key, value)
    }

    actual fun getSecureDouble(key: String, default: Double): Double {
        return encryptedPreferences?.getDoubleOrNull(key) ?: default
    }

    actual fun putFloat(key: String, value: Float) {
        preferences?.putFloat(key, value)
    }

    actual fun getFloat(key: String, default: Float): Float {
        return preferences?.getFloatOrNull(key) ?: default
    }

    actual fun putSecureFloat(key: String, value: Float) {
        encryptedPreferences?.putFloat(key, value)
    }

    actual fun getSecureFloat(key: String, default: Float): Float {
        return encryptedPreferences?.getFloatOrNull(key) ?: default
    }

    actual fun putLong(key: String, value: Long) {
        preferences?.putLong(key, value)
    }

    actual fun getLong(key: String, default: Long): Long {
        return preferences?.getLongOrNull(key) ?: default
    }

    actual fun putSecureLong(key: String, value: Long) {
        encryptedPreferences?.putLong(key, value)
    }

    actual fun getSecureLong(key: String, default: Long): Long {
        return encryptedPreferences?.getLongOrNull(key) ?: default
    }

    actual fun putBool(key: String, value: Boolean) {
        preferences?.putBoolean(key, value)
    }

    actual fun getBool(key: String, default: Boolean): Boolean {
        return preferences?.getBooleanOrNull(key) ?: default
    }

    actual fun putSecureBool(key: String, value: Boolean) {
        encryptedPreferences?.putBoolean(key, value)
    }

    actual fun getSecureBool(key: String, default: Boolean): Boolean {
        return encryptedPreferences?.getBooleanOrNull(key) ?: default
    }

    actual fun contains(key: String, isSecure: Boolean): Boolean {
        return if (isSecure) {
            encryptedPreferences?.hasKey(key) ?: false
        } else {
            preferences?.hasKey(key) ?: false
        }
    }

    actual fun clearAll() {
        clearSecuredValues()
        clearAllUserValues()
    }

    actual fun clearSecuredValues() {
        encryptedPreferences?.clear()
    }

    actual fun clearAllUserValues() {
        preferences?.clear()
    }

    actual fun clearValue(key: String) {
        preferences?.remove(key)
    }

    actual fun clearSecureValue(key: String) {
        encryptedPreferences?.remove(key)
    }
}