package com.swensonhe.strapikmm.backgrounddownloader

actual interface DownloadStatusListener: DownloadListener {
    fun onDownloadError(downloadInfo: DownloadInfo, error: Throwable)
}