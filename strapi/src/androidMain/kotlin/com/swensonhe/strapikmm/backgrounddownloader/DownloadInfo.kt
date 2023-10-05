package com.swensonhe.strapikmm.backgrounddownloader

actual data class DownloadInfo(
    val id: Int,
    val url: String,
    val path: String,
    val downloaded: Long,
    val total: Long,
    val progress: Int
)