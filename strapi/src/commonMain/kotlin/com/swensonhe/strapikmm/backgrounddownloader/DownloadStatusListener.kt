package com.swensonhe.strapikmm.backgrounddownloader

interface DownloadListener {
    fun onDownloadStart(downloadInfo: DownloadInfo)
    fun onDownloadProgress(downloadInfo: DownloadInfo)
    fun onDownloadDone(downloadInfo: DownloadInfo)
}

expect interface DownloadStatusListener: DownloadListener