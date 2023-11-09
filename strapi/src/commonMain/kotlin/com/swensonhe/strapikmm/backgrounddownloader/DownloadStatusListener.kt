package com.swensonhe.strapikmm.backgrounddownloader

/**
 * An interface for listening to download events and status updates during a download operation.
 * Implement this interface to receive notifications about the progress and completion of downloads.
 */
interface DownloadListener {
    /**
     * Called when a download operation starts.
     *
     * @param downloadInfo Information about the download that has started.
     */
    fun onDownloadStart(downloadInfo: DownloadInfo)

    /**
     * Called periodically to report download progress.
     *
     * @param downloadInfo Information about the download, including progress details.
     */
    fun onDownloadProgress(downloadInfo: DownloadInfo)

    /**
     * Called when a download operation is successfully completed.
     *
     * @param downloadInfo Information about the download that has completed.
     */
    fun onDownloadDone(downloadInfo: DownloadInfo)
}

/**
 * An expect interface that extends the [DownloadListener] interface.
 * Platform-specific implementations of this interface should be provided for each platform (e.g., Web, Android, iOS).
 * Use this interface to define a listener for download status updates.
 */
expect interface DownloadStatusListener : DownloadListener
