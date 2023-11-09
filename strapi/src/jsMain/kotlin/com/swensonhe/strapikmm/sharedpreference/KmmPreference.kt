@file:OptIn(ExperimentalJsExport::class)

package com.swensonhe.strapikmm.sharedpreference
import com.russhwolf.settings.Settings

/**
 * A class for handling shared preferences.
 *
 * @param preferences An instance of [Settings] used for storing user preferences.
 * @param encryptedPreferences An instance of [Settings] used for storing sensitive user preferences.
 * **Note: in Android we are not using [EncryptedPreferences] and we saving everything in default preferences**.
 */
@JsExport
actual class KmmPreference actual constructor(
    private val preferences: Settings?,
    private val encryptedPreferences: Settings?
) {
    actual fun putInt(key: String, value: Int) {
        putSecureInt(key, value)
    }

    actual fun getInt(key: String, default: Int): Int {
        return getSecureInt(key, default)
    }

    actual fun putSecureInt(key: String, value: Int) {
        preferences?.putInt(key, value)
    }

    actual fun getSecureInt(key: String, default: Int): Int {
        return preferences?.getIntOrNull(key) ?: default
    }

    actual fun putString(key: String, value: String) {
        putSecureString(key, value)
    }

    actual fun getString(key: String): String? {
        return getSecureString(key)
    }

    actual fun putSecureString(key: String, value: String) {
        preferences?.putString(key, value)
    }

    actual fun getSecureString(key: String): String? {
        return preferences?.getStringOrNull(key)
    }

    actual fun putDouble(key: String, value: Double) {
        putSecureDouble(key, value)
    }

    actual fun getDouble(key: String, default: Double): Double {
        return getSecureDouble(key, default)
    }

    actual fun putSecureDouble(key: String, value: Double) {
        preferences?.putDouble(key, value)
    }

    actual fun getSecureDouble(key: String, default: Double): Double {
        return preferences?.getDoubleOrNull(key) ?: default
    }

    actual fun putFloat(key: String, value: Float) {
        putSecureFloat(key, value)
    }

    actual fun getFloat(key: String, default: Float): Float {
        return getSecureFloat(key, default)
    }

    actual fun putSecureFloat(key: String, value: Float) {
        preferences?.putFloat(key, value)
    }

    actual fun getSecureFloat(key: String, default: Float): Float {
        return preferences?.getFloatOrNull(key) ?: default
    }

    actual fun putLong(key: String, value: Long) {
        putSecureLong(key, value)
    }

    actual fun getLong(key: String, default: Long): Long {
        return getSecureLong(key, default)
    }

    actual fun putSecureLong(key: String, value: Long) {
            preferences?.putLong(key, value)
    }

    actual fun getSecureLong(key: String, default: Long): Long {
        return preferences?.getLongOrNull(key) ?: default
    }

    actual fun putBool(key: String, value: Boolean) {
        putSecureBool(key, value)
    }

    actual fun getBool(key: String, default: Boolean): Boolean {
        return getSecureBool(key, default)
    }

    actual fun putSecureBool(key: String, value: Boolean) {
        preferences?.putBoolean(key, value)
    }

    actual fun getSecureBool(key: String, default: Boolean): Boolean {
        return preferences?.getBooleanOrNull(key) ?: default
    }

    actual fun contains(key: String, isSecure: Boolean): Boolean {
        return preferences?.hasKey(key) ?: false
    }

    actual fun clearAll() {
        clearSecuredValues()
        clearAllUserValues()
    }

    actual fun clearSecuredValues() {
            preferences?.clear()
    }

    actual fun clearAllUserValues() {
        clearSecuredValues()
    }

    actual fun clearValue(key: String) {
        clearSecureValue(key)
    }

    actual fun clearSecureValue(key: String) {
            preferences?.remove(key)
    }
}