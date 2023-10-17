package com.swensonhe.strapikmm.backgrounddownloader

import com.tonyodev.fetch2.Error

actual interface DownloadStatusListener: DownloadListener {
    fun onDownloadError(downloadInfo: DownloadInfo, error: Error)
    fun onDownloadCancelled(downloadInfo: DownloadInfo)
    fun onDownloadPaused(downloadInfo: DownloadInfo)
}