package com.metacto.strapikmm.backgrounddownloader

import platform.Foundation.NSURL

actual data class DownloadInfo(
    val id: String,
    val cacheURL: NSURL?,
    val downloadURL: NSURL?,
    val progress: Double,
    val isDownloadComplete: Boolean
)