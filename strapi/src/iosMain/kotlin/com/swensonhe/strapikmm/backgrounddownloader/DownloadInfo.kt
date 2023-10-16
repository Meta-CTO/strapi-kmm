package com.swensonhe.strapikmm.backgrounddownloader

actual data class DownloadInfo(
    val id: String,
    val url: String?,
    val total: Long,
    val progress: Int,
    val isDownloadComplete: Boolean
)