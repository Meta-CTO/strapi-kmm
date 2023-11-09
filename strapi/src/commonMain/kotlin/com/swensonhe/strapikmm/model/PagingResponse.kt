@file:OptIn(ExperimentalJsExport::class)
package com.swensonhe.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a paging response containing a list of data and metadata.
 *
 * @param data The list of data items.
 * @param meta The metadata for paging information.
 */
@Serializable
class PagingResponse<T>(
    @SerialName("data")
    val data: List<T>,
    @SerialName("meta")
    val meta: MetaResponse?,
)

/**
 * Wraps a single data item.
 *
 * @param data The wrapped data item.
 */
@Serializable
@JsExport
class DataWrapper<T>(
    @SerialName("data")
    val data: T,
)

/**
 * Represents metadata information for paging.
 *
 * @param pagination The pagination details.
 */
@Serializable
@JsExport
class MetaResponse(
    @SerialName("pagination")
    val pagination: Paging?
)

/**
 * Represents details about paging information.
 *
 * @param page The current page number.
 * @param pageSize The number of items per page.
 * @param pageCount The total number of pages.
 * @param total The total number of items.
 */
@Serializable
@JsExport
class Paging(
    @SerialName("page")
    val page: Int,
    @SerialName("pageSize")
    val pageSize: Int,
    @SerialName("pageCount")
    val pageCount: Int,
    @SerialName("total")
    val total: Int
)

