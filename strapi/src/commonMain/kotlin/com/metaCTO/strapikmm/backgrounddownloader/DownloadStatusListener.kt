package com.metaCTO.strapikmm.backgrounddownloader

interface DownloadListener {
    fun onDownloadStart(downloadInfo: DownloadInfo)
    fun onDownloadProgress(downloadInfo: DownloadInfo)
    fun onDownloadDone(downloadInfo: DownloadInfo)
}

expect interface DownloadStatusListener: DownloadListener