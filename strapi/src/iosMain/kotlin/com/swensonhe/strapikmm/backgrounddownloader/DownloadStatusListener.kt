package com.swensonhe.strapikmm.backgrounddownloader

import platform.Foundation.NSError

/**
 * The [DownloadStatusListener] interface defines extra callbacks for monitoring the status and progress of download operations.
 * It extends the [DownloadListener] interface, which provides basic download-related callbacks.
 */
actual interface DownloadStatusListener: DownloadListener {
    /**
     * Called when an unfinished download operation is resumed.
     *
     * @param downloadInfo Information about the download, including its unique identifier, download URL, progress, and completion status.
     */
    fun onUnfinishedDownloadStart(downloadInfo: DownloadInfo)
    /**
     * Called when an error occurs during a download operation.
     *
     * @param downloadInfo Information about the download, including its unique identifier, download URL, progress, and completion status.
     * @param error The [NSError] object containing details about the error.
     */
    fun onDownloadError(downloadInfo: DownloadInfo, error: NSError)
    /**
     * Called when there is an error while attempting to resume unfinished downloads.
     *
     * @param error The [NSError] object containing details about the error encountered when resuming unfinished downloads.
     */
    fun onResumeUnfinishedDownloadsError(error: NSError)
}