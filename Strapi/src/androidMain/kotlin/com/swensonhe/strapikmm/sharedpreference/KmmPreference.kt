package com.swensonhe.strapikmm.sharedpreference

import com.liftric.kvault.KVault

actual class KmmPreference actual constructor(private val encryptedPreferences: KVault) {
    actual fun putInt(key: String, value: Int) {
        putSecureInt(key, value)
    }

    actual fun getInt(key: String, default: Int): Int {
        return getSecureInt(key, default)
    }

    actual fun putSecureInt(key: String, value: Int) {
        encryptedPreferences.set(key, value)
    }

    actual fun getSecureInt(key: String, default: Int): Int {
        return encryptedPreferences.int(key) ?: default
    }

    actual fun putString(key: String, value: String) {
        putSecureString(key, value)
    }

    actual fun getString(key: String): String? {
        return getSecureString(key)
    }

    actual fun putSecureString(key: String, value: String) {
        encryptedPreferences.set(key, value)
    }

    actual fun getSecureString(key: String): String? {
        return encryptedPreferences.string(key)
    }

    actual fun putDouble(key: String, value: Double) {
        putSecureDouble(key, value)
    }

    actual fun getDouble(key: String, default: Double): Double {
        return getSecureDouble(key, default)
    }

    actual fun putSecureDouble(key: String, value: Double) {
        encryptedPreferences.set(key, value)
    }

    actual fun getSecureDouble(key: String, default: Double): Double {
        return encryptedPreferences.double(key) ?: default
    }

    actual fun putFloat(key: String, value: Float) {
        putSecureFloat(key, value)
    }

    actual fun getFloat(key: String, default: Float): Float {
        return getSecureFloat(key, default)
    }

    actual fun putSecureFloat(key: String, value: Float) {
        encryptedPreferences.set(key, value)
    }

    actual fun getSecureFloat(key: String, default: Float): Float {
        return encryptedPreferences.float(key) ?: default
    }

    actual fun putLong(key: String, value: Long) {
        putSecureLong(key, value)
    }

    actual fun getLong(key: String, default: Long): Long {
        return getSecureLong(key, default)
    }

    actual fun putSecureLong(key: String, value: Long) {
        encryptedPreferences.set(key, value)
    }

    actual fun getSecureLong(key: String, default: Long): Long {
        return encryptedPreferences.long(key) ?: default
    }

    actual fun putBool(key: String, value: Boolean) {
        putSecureBool(key, value)
    }

    actual fun getBool(key: String, default: Boolean): Boolean {
        return getSecureBool(key, default)
    }

    actual fun putSecureBool(key: String, value: Boolean) {
        encryptedPreferences.set(key, value)
    }

    actual fun getSecureBool(key: String, default: Boolean): Boolean {
        return encryptedPreferences.bool(key) ?: default
    }

    actual fun contains(key: String, isSecure: Boolean): Boolean {
        return encryptedPreferences.existsObject(key)
    }

    actual fun clearAll() {
        clearSecuredValues()
        clearAllUserValues()
    }

    actual fun clearSecuredValues() {
        encryptedPreferences.clear()
    }

    actual fun clearAllUserValues() {
        clearSecuredValues()
    }

    actual fun clearValue(key: String) {
        clearSecureValue(key)
    }

    actual fun clearSecureValue(key: String) {
        encryptedPreferences.deleteObject(key)
    }
}