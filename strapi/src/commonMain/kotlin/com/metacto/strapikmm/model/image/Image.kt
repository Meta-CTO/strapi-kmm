@file:OptIn(ExperimentalSerializationApi::class)

package com.metacto.strapikmm.model.image

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlin.js.JsExport

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
