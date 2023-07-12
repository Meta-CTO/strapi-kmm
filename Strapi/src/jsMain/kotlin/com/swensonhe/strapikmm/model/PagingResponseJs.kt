@file:OptIn(ExperimentalJsExport::class)

package com.swensonhe.strapikmm.model

@JsExport
data class PagingResponseJs<T>(
    val data: Array<T>?,
    val meta: MetaResponse?,
)

inline fun <reified T> PagingResponse<T>.toPagingResponseJs(): PagingResponseJs<T> {
    return PagingResponseJs(
        data = data.toTypedArray(),
        meta = meta
    )
}