package com.swensonhe.strapikmm.backgrounddownloader

import platform.Foundation.NSError

actual interface DownloadStatusListener: DownloadListener {
    fun onUnfinishedDownloadStart(downloadInfo: DownloadInfo)
    fun onDownloadError(downloadInfo: DownloadInfo, error: NSError)
    fun onResumeUnfinishedDownloadsError(error: NSError)
}