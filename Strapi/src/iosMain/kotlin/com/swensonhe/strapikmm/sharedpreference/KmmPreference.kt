package com.swensonhe.strapikmm.sharedpreference

import com.liftric.kvault.KVault
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

actual class KmmPreference actual constructor(private val kVault: KVault) {
    actual fun putInt(key: String, value: Int) {
        NSUserDefaults.standardUserDefaults.setInteger(value.toLong(), key)
    }

    actual fun getInt(key: String, default: Int): Int {
        return (NSUserDefaults.standardUserDefaults.objectForKey(key) as? Long?
            ?: default.toLong()).toInt()
    }

    actual fun putSecureInt(key: String, value: Int) {
        kVault.set(key, value)
    }

    actual fun getSecureInt(key: String, default: Int): Int {
        return kVault.int(key) ?: default
    }

    actual fun putString(key: String, value: String) {
        NSUserDefaults.standardUserDefaults.setObject(value, key)
    }

    actual fun getString(key: String): String? {
        return NSUserDefaults.standardUserDefaults.stringForKey(key)
    }

    actual fun putSecureString(key: String, value: String) {
        kVault.set(key, value)
    }

    actual fun getSecureString(key: String): String? {
        return kVault.string(key)
    }

    actual fun putDouble(key: String, value: Double) {
        NSUserDefaults.standardUserDefaults.setDouble(value, key)
    }

    actual fun getDouble(key: String, default: Double): Double {
        return NSUserDefaults.standardUserDefaults.objectForKey(key) as? Double? ?: default
    }

    actual fun putSecureDouble(key: String, value: Double) {
        kVault.set(key, value)
    }

    actual fun getSecureDouble(key: String, default: Double): Double {
        return kVault.double(key) ?: default
    }

    actual fun putFloat(key: String, value: Float) {
        NSUserDefaults.standardUserDefaults.setFloat(value, key)
    }

    actual fun getFloat(key: String, default: Float): Float {
        return NSUserDefaults.standardUserDefaults.objectForKey(key) as? Float? ?: default
    }

    actual fun putSecureFloat(key: String, value: Float) {
        kVault.set(key, value)
    }

    actual fun getSecureFloat(key: String, default: Float): Float {
        return kVault.float(key) ?: default
    }

    actual fun putLong(key: String, value: Long) {
        NSUserDefaults.standardUserDefaults.setInteger(value, key)
    }

    actual fun getLong(key: String, default: Long): Long {
        return NSUserDefaults.standardUserDefaults.objectForKey(key) as? Long? ?: default
    }

    actual fun putSecureLong(key: String, value: Long) {
        kVault.set(key, value)
    }

    actual fun getSecureLong(key: String, default: Long): Long {
        return kVault.long(key) ?: default
    }

    actual fun putBool(key: String, value: Boolean) {
        NSUserDefaults.standardUserDefaults.setBool(value, key)
    }

    actual fun getBool(key: String, default: Boolean): Boolean {
        return NSUserDefaults.standardUserDefaults.objectForKey(key) as? Boolean? ?: default
    }

    actual fun putSecureBool(key: String, value: Boolean) {
        kVault.set(key, value)
    }

    actual fun getSecureBool(key: String, default: Boolean): Boolean {
        return kVault.bool(key) ?: default
    }

    actual fun contains(key: String, isSecure: Boolean): Boolean {
        return if (isSecure) {
            kVault.existsObject(key)
        } else {
            NSUserDefaults.standardUserDefaults.objectForKey(key) != null
        }
    }

    actual fun clearAll() {
        clearSecuredValues()
        clearAllUserValues()
    }

    actual fun clearSecuredValues() {
        kVault.clear()
    }

    actual fun clearAllUserValues() {
        val appDomain = NSBundle.mainBundle().bundleIdentifier.orEmpty()
        NSUserDefaults.standardUserDefaults.removePersistentDomainForName(appDomain)
    }

    actual fun clearValue(key: String) {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(key)
    }

    actual fun clearSecureValue(key: String) {
        kVault.deleteObject(key)
    }
}