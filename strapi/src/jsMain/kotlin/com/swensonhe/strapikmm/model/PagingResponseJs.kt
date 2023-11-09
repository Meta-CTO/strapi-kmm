@file:OptIn(ExperimentalJsExport::class)

package com.swensonhe.strapikmm.model

/**
 * Data class representing a paging response for JavaScript (JsExport).
 *
 * @property data An array of type [T] representing the paged data.
 * @property meta A [MetaResponse] containing metadata information about the paging response.
 */
@JsExport
data class PagingResponseJs<T>(
    val data: Array<T>?,
    val meta: MetaResponse?,
)

/**
 * Extension function to convert a [PagingResponse] to a [PagingResponseJs].
 *
 * @return A [PagingResponseJs] with data and metadata converted from the original [PagingResponse].
 */
inline fun <reified T> PagingResponse<T>.toPagingResponseJs(): PagingResponseJs<T> {
    return PagingResponseJs(
        data = data.toTypedArray(),
        meta = meta
    )
}