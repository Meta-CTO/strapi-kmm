package com.swensonhe.strapikmm.sharedpreference

import com.russhwolf.settings.Settings

/**
 * Interface for key-value storage, providing methods to store and retrieve data of various types.
 * Platform-specific implementations are expected to provide concrete implementations of these methods.
 *
 * @property preferences Platform-specific settings for non-encrypted preferences.
 * @property encryptedPreferences Platform-specific settings for encrypted preferences.
 */
expect class KmmPreference(
    preferences: Settings? = null,
    encryptedPreferences: Settings? = null
) {

    /**
     * Store an integer value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The integer value to store.
     */
    fun putInt(key: String, value: Int)

    /**
     * Retrieve an integer value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @param default The default value to return if the key is not found.
     * @return The stored integer value or the default value if the key is not found.
     */
    fun getInt(key: String, default: Int): Int

    /**
     * Store an encrypted integer value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The integer value to store.
     */
    fun putSecureInt(key: String, value: Int)

    /**
     * Retrieve an encrypted integer value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @param default The default value to return if the key is not found.
     * @return The stored integer value or the default value if the key is not found.
     */
    fun getSecureInt(key: String, default: Int): Int

    /**
     * Store a string value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The string value to store.
     */
    fun putString(key: String, value: String)

    /**
     * Retrieve a string value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @return The stored string value or null if the key is not found.
     */
    fun getString(key: String): String?

    /**
     * Store an encrypted string value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The string value to store.
     */
    fun putSecureString(key: String, value: String)

    /**
     * Retrieve an encrypted string value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @return The stored string value or null if the key is not found.
     */
    fun getSecureString(key: String): String?

    /**
     * Store a double value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The double value to store.
     */
    fun putDouble(key: String, value: Double)

    /**
     * Retrieve a double value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @param default The default value to return if the key is not found.
     * @return The stored double value or the default value if the key is not found.
     */
    fun getDouble(key: String, default: Double): Double

    /**
     * Store an encrypted double value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The double value to store.
     */
    fun putSecureDouble(key: String, value: Double)

    /**
     * Retrieve an encrypted double value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @param default The default value to return if the key is not found.
     * @return The stored double value or the default value if the key is not found.
     */
    fun getSecureDouble(key: String, default: Double): Double

    /**
     * Store a float value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The float value to store.
     */
    fun putFloat(key: String, value: Float)

    /**
     * Retrieve a float value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @param default The default value to return if the key is not found.
     * @return The stored float value or the default value if the key is not found.
     */
    fun getFloat(key: String, default: Float): Float

    /**
     * Store an encrypted float value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The float value to store.
     */
    fun putSecureFloat(key: String, value: Float)

    /**
     * Retrieve an encrypted float value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @param default The default value to return if the key is not found.
     * @return The stored float value or the default value if the key is not found.
     */
    fun getSecureFloat(key: String, default: Float): Float

    /**
     * Store a long value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The long value to store.
     */
    fun putLong(key: String, value: Long)

    /**
     * Retrieve a long value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @param default The default value to return if the key is not found.
     * @return The stored long value or the default value if the key is not found.
     */
    fun getLong(key: String, default: Long): Long

    /**
     * Store an encrypted long value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The long value to store.
     */
    fun putSecureLong(key: String, value: Long)

    /**
     * Retrieve an encrypted long value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @param default The default value to return if the key is not found.
     * @return The stored long value or the default value if the key is not found.
     */
    fun getSecureLong(key: String, default: Long): Long

    /**
     * Store a boolean value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The boolean value to store.
     */
    fun putBool(key: String, value: Boolean)

    /**
     * Retrieve a boolean value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @param default The default value to return if the key is not found.
     * @return The stored boolean value or the default value if the key is not found.
     */
    fun getBool(key: String, default: Boolean): Boolean

    /**
     * Store an encrypted boolean value in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param value The boolean value to store.
     */
    fun putSecureBool(key: String, value: Boolean)

    /**
     * Retrieve an encrypted boolean value from preferences.
     *
     * @param key The unique identifier for the preference.
     * @param default The default value to return if the key is not found.
     * @return The stored boolean value or the default value if the key is not found.
     */
    fun getSecureBool(key: String, default: Boolean): Boolean

    /**
     * Remove a value from preferences.
     *
     * @param key The unique identifier for the preference.
     */
    fun clearValue(key: String)

    /**
     * Remove an encrypted value from preferences.
     *
     * @param key The unique identifier for the preference.
     */
    fun clearSecureValue(key: String)

    /**
     * Remove all values from preferences.
     */
    fun clearAll()

    /**
     * Remove all encrypted values from preferences.
     */
    fun clearSecuredValues()

    /**
     * Remove all user values from preferences.
     */
    fun clearAllUserValues()

    /**
     * Check if a value exists in preferences.
     *
     * @param key The unique identifier for the preference.
     * @param isSecure Whether the value is encrypted or not.
     * @return True if the value exists, false otherwise.
     */
    fun contains(key: String, isSecure: Boolean): Boolean
}
