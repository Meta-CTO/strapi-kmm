package com.metacto.strapikmm.sharedpreference



import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> KmmPreference.getObject(key: String): T? {
    return try {
        val serializedObj = getString(key)
        serializedObj?.let { Json.decodeFromString(it) }
    } catch (_: Throwable) {
        null
    }
}

inline fun <reified T> KmmPreference.putObject(key: String, value: T) {
    val serializedObj = Json.encodeToString(value)
    putString(key, serializedObj)
}