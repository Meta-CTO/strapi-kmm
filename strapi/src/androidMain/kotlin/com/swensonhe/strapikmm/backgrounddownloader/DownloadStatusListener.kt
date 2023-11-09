package com.swensonhe.strapikmm.backgrounddownloader

import com.tonyodev.fetch2.Error

/**
 * The [DownloadStatusListener] interface defines extra callbacks for monitoring the status and progress of download operations.
 * It extends the [DownloadListener] interface, which provides basic download-related callbacks.
 */
actual interface DownloadStatusListener: DownloadListener {
    /**
     * Called when a download encounters an error.
     *
     * @param downloadInfo The information about the download.
     * @param error The error that occurred during the download.
     */
    fun onDownloadError(downloadInfo: DownloadInfo, error: Error)

    /**
     * Called when a download is canceled or removed.
     *
     * @param downloadInfo The information about the download.
     */
    fun onDownloadCancelled(downloadInfo: DownloadInfo)

    /**
     * Called when a download is paused.
     *
     * @param downloadInfo The information about the download.
     */
    fun onDownloadPaused(downloadInfo: DownloadInfo)
}