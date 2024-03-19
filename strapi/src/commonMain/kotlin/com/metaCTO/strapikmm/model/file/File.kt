@file:OptIn(ExperimentalSerializationApi::class)

package com.metaCTO.strapikmm.model.file

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlin.js.JsExport

@Serializable
@JsExport
data class File(
    @SerialName("id")
    val id: Int? = null,
    @JsonNames("attributes.url", "url")
    val fileUrl: String? = null,
    @JsonNames("attributes.name", "name")
    val fileName: String? = null,
    @JsonNames("attributes.mime", "mime")
    val mime: String? = null,
    @JsonNames("attributes.previewUrl", "previewUrl")
    val previewUrl: String? = null,
    @JsonNames("attributes.width", "width")
    val width: Int? = null,
    @JsonNames("attributes.height", "height")
    val height: Int? = null,
    @JsonNames("attributes.ext", "ext")
    val ext: String? = null,
    @JsonNames("attributes.size", "size")
    val size: Double? = null
)

@Serializable
data class UploadFileRequest(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("name")
    val name: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("mime")
    val mime: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("width")
    val width: Int? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("height")
    val height: Int? = null,
    @SerialName("preview_url")
    val previewUrl: String = "",
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("ext")
    val ext: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("size")
    val size: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("url")
    val url: String? = null,
)

@Serializable
@Suppress()
data class UploadFiles(
    @SerialName("files")
    val data: List<UploadFileRequest>
)
