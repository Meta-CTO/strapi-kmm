package com.metaCTO.strapikmm.backgrounddownloader

actual interface DownloadStatusListener: DownloadListener {
    fun onDownloadError(downloadInfo: DownloadInfo, error: Throwable)
}