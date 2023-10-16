package com.swensonhe.strapikmm.backgrounddownloader

interface DownloadListener {
    fun onDownloadStart(downloadInfo: DownloadInfo)
    fun onDownloading(downloadInfo: DownloadInfo)
    fun onDownloadDone(downloadInfo: DownloadInfo)
    fun onDownloadProgress(downloadInfo: DownloadInfo)
}

expect interface DownloadStatusListener: DownloadListener