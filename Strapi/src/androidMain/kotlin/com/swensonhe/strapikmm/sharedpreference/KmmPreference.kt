package com.swensonhe.strapikmm.sharedpreference

import com.liftric.kvault.KVault

actual class KmmPreference actual constructor(private val kVault: KVault) {
    actual fun putInt(key: String, value: Int) {
        putSecureInt(key, value)
    }

    actual fun getInt(key: String, default: Int): Int {
        return getSecureInt(key, default)
    }

    actual fun putSecureInt(key: String, value: Int) {
        kVault.set(key, value)
    }

    actual fun getSecureInt(key: String, default: Int): Int {
        return kVault.int(key) ?: default
    }

    actual fun putString(key: String, value: String) {
        putSecureString(key, value)
    }

    actual fun getString(key: String): String? {
        return getSecureString(key)
    }

    actual fun putSecureString(key: String, value: String) {
        kVault.set(key, value)
    }

    actual fun getSecureString(key: String): String? {
        return kVault.string(key)
    }

    actual fun putDouble(key: String, value: Double) {
        putSecureDouble(key, value)
    }

    actual fun getDouble(key: String, default: Double): Double {
        return getSecureDouble(key, default)
    }

    actual fun putSecureDouble(key: String, value: Double) {
        kVault.set(key, value)
    }

    actual fun getSecureDouble(key: String, default: Double): Double {
        return kVault.double(key) ?: default
    }

    actual fun putFloat(key: String, value: Float) {
        putSecureFloat(key, value)
    }

    actual fun getFloat(key: String, default: Float): Float {
        return getSecureFloat(key, default)
    }

    actual fun putSecureFloat(key: String, value: Float) {
        kVault.set(key, value)
    }

    actual fun getSecureFloat(key: String, default: Float): Float {
        return kVault.float(key) ?: default
    }

    actual fun putLong(key: String, value: Long) {
        putSecureLong(key, value)
    }

    actual fun getLong(key: String, default: Long): Long {
        return getSecureLong(key, default)
    }

    actual fun putSecureLong(key: String, value: Long) {
        kVault.set(key, value)
    }

    actual fun getSecureLong(key: String, default: Long): Long {
        return kVault.long(key) ?: default
    }

    actual fun putBool(key: String, value: Boolean) {
        putSecureBool(key, value)
    }

    actual fun getBool(key: String, default: Boolean): Boolean {
        return getSecureBool(key, default)
    }

    actual fun putSecureBool(key: String, value: Boolean) {
        kVault.set(key, value)
    }

    actual fun getSecureBool(key: String, default: Boolean): Boolean {
        return kVault.bool(key) ?: default
    }

    actual fun contains(key: String, isSecure: Boolean): Boolean {
        return kVault.existsObject(key)
    }

    actual fun clearAll() {
        clearSecuredValues()
        clearAllUserValues()
    }

    actual fun clearSecuredValues() {
        kVault.clear()
    }

    actual fun clearAllUserValues() {
        clearSecuredValues()
    }

    actual fun clearValue(key: String) {
        clearSecureValue(key)
    }

    actual fun clearSecureValue(key: String) {
        kVault.deleteObject(key)
    }
}