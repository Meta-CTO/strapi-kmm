package com.metacto.strapikmm.deeplink

import org.json.JSONObject

fun JSONObject.toMap(): Map<Any?, *> {
    val map = mutableMapOf<Any?, Any?>()
    keys().forEach {
        map[it] = get(it)
    }

    return map
}