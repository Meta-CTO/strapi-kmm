@file:OptIn(ExperimentalJsExport::class)
package com.metaCTO.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@Serializable
class PagingResponse<T>(
    @SerialName("data")
    val data: List<T>,
    @SerialName("meta")
    val meta: MetaResponse?,
)

@Serializable
@JsExport
class DataWrapper<T>(
    @SerialName("data")
    val data: T,
)

@Serializable
@JsExport
class MetaResponse(
    @SerialName("pagination")
    val pagination: Paging?
)

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

