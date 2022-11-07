package com.swensonhe.strapikmm.sharedpreference

import com.liftric.kvault.KVault

expect class KmmPreference(encryptedPreferences: KVault) {

    fun putInt(key: String, value: Int)

    fun getInt(key: String, default: Int): Int

    fun putSecureInt(key: String, value: Int)

    fun getSecureInt(key: String, default: Int): Int

    fun putString(key: String, value: String)

    fun getString(key: String): String?

    fun putSecureString(key: String, value: String)

    fun getSecureString(key: String): String?

    fun putDouble(key: String, value: Double)

    fun getDouble(key: String, default: Double): Double

    fun putSecureDouble(key: String, value: Double)

    fun getSecureDouble(key: String, default: Double): Double

    fun putFloat(key: String, value: Float)

    fun getFloat(key: String, default: Float): Float

    fun putSecureFloat(key: String, value: Float)

    fun getSecureFloat(key: String, default: Float): Float

    fun putLong(key: String, value: Long)

    fun getLong(key: String, default: Long): Long

    fun putSecureLong(key: String, value: Long)

    fun getSecureLong(key: String, default: Long): Long

    fun putBool(key: String, value: Boolean)

    fun getBool(key: String, default: Boolean): Boolean

    fun putSecureBool(key: String, value: Boolean)

    fun getSecureBool(key: String, default: Boolean): Boolean

    fun clearValue(key: String)

    fun clearSecureValue(key: String)

    fun clearAll()

    fun clearSecuredValues()

    fun clearAllUserValues()

    fun contains(key: String, isSecure: Boolean): Boolean
}
