@file:OptIn(ExperimentalSerializationApi::class)

package com.swensonhe.strapikmm.model.file

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlin.js.JsExport

/**
 * Represents the uploaded file with its attributes.
 *
 * @param id The unique identifier of the file.
 * @param fileUrl The URL of the file.
 * @param fileName The name of the file.
 * @param mime The MIME type of the file.
 */
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
    val mime: String? = null
)

/**
 * Represents a request to upload a file with its attributes.
 *
 * @param name The name of the file.
 * @param mime The MIME type of the file.
 * @param ext The file extension.
 * @param size The size of the file.
 * @param url The URL of the file.
 */
@Serializable
data class UploadFileRequest(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("name")
    val name: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("mime")
    val mime: String? = null,
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


/**
 * Represents a collection of files to be uploaded.
 *
 * @param data The list of [UploadFileRequest] objects representing the files to be uploaded.
 */
@Serializable
@Suppress()
data class UploadFiles(
    @SerialName("files")
    val data: List<UploadFileRequest>
)
