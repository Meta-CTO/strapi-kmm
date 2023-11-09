@file:OptIn(ExperimentalSerializationApi::class)

package com.swensonhe.strapikmm.model.image

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlin.js.JsExport

/**
 * Represents the strapi image with its attributes.
 *
 * @param id The unique identifier of the image.
 * @param name The name of the image.
 * @param mime The MIME type of the image.
 * @param url The URL of the image.
 * @param largeUrl The URL of the image in a large format, if available.
 */
@Serializable
@JsExport
data class Image(
    @JsonNames("attributes.id", "id")
    val id: Int? = null,
    @JsonNames("attributes.name", "name")
    val name: String? = null,
    @JsonNames("attributes.mime", "mime")
    val mime: String? = null,
    @JsonNames("attributes.url", "url")
    val url: String? = null,
    @JsonNames("attributes.formats.large.url", "formats.large.url")
    val largeUrl: String? = null
) {
    fun getImageUrl() = largeUrl ?: url
}
