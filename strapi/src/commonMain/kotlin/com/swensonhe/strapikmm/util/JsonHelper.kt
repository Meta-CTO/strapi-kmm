package com.swensonhe.strapikmm.util

import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
object JsonHelper {
    @Throws(Throwable::class)
    fun <T> constructModelFromString(string: String, serializer: KSerializer<T>): T {
        return JsonWithIgnoredUnknownKeys.decodeFromString(serializer, string)
    }

    @Throws(Throwable::class)
    fun <T> convertToJsonString(model: T, serializer: KSerializer<T>): String {
        return Json.encodeToString(serializer, model)
    }
}
