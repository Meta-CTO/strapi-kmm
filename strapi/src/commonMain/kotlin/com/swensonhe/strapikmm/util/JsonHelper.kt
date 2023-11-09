package com.swensonhe.strapikmm.util

import com.swensonhe.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport


/**
 * A utility object for working with JSON serialization and deserialization.
 *
 * This object provides functions for constructing models from JSON strings and converting models to JSON strings.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
object JsonHelper {
    /**
     * Constructs a model of type [T] from a JSON string using the specified serializer.
     *
     * @param string The JSON string to deserialize.
     * @param serializer The KSerializer representing the type [T].
     * @return The deserialized model of type [T].
     * @throws Throwable if deserialization fails.
     */
    @Throws(Throwable::class)
    fun <T> constructModelFromString(string: String, serializer: KSerializer<T>): T {
        return JsonWithIgnoredUnknownKeys.decodeFromString(serializer, string)
    }

    /**
     * Converts a model of type [T] to a JSON string using the specified serializer.
     *
     * @param model The model to be serialized.
     * @param serializer The KSerializer representing the type [T].
     * @return The JSON string representing the serialized model.
     * @throws Throwable if serialization fails.
     */
    @Throws(Throwable::class)
    fun <T> convertToJsonString(model: T, serializer: KSerializer<T>): String {
        return Json.encodeToString(serializer, model)
    }
}
