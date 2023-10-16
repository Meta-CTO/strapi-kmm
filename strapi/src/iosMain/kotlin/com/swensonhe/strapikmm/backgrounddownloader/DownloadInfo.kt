package com.swensonhe.strapikmm.backgrounddownloader

import platform.Foundation.NSURL

actual data class DownloadInfo(
    val id: String,
    val url: NSURL?,
    val progress: Double,
    val isDownloadComplete: Boolean
)