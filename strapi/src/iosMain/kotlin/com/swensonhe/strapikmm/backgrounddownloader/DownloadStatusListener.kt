package com.swensonhe.strapikmm.backgrounddownloader

actual interface DownloadStatusListener: DownloadListener {

    // You can add more methods here if you want to that related to iOS only.
    // Or you can customize the method signature to match the iOS one.
    fun onDownloadError(downloadInfo: DownloadInfo, error: Throwable)
}