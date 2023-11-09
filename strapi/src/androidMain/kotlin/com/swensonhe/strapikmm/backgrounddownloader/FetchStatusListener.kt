package com.swensonhe.strapikmm.backgrounddownloader

import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock

/**
 * An internal class that listens to the status changes of download tasks and communicates
 * with the [DownloadStatusListener] to provide updates and handle download events.
 *
 * @param downloadStatusListener The listener to notify about download status changes.
 * @param stopNotificationServiceIfRequired A function to stop the notification service if required.
 * @param startDownloadServiceIfRequired A function to start the download service if required.
 */
internal class FetchStatusListener(
    private val downloadStatusListener: DownloadStatusListener,
    private val stopNotificationServiceIfRequired: () -> Unit,
    private val startDownloadServiceIfRequired: () -> Unit
) : FetchListener {

    /**
     * Called when a download task is added to the queue.
     */
    override fun onAdded(download: Download) {
        downloadStatusListener.onDownloadStart(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    /**
     * Called when a download task is cancelled.
     */
    override fun onCancelled(download: Download) {
        downloadStatusListener.onDownloadCancelled(download.toDownloadInfo())
        stopNotificationServiceIfRequired.invoke()
    }

    /**
     * Called when a download task is completed.
     */
    override fun onCompleted(download: Download) {
        downloadStatusListener.onDownloadDone(download.toDownloadInfo())
        stopNotificationServiceIfRequired.invoke()
    }

    /**
     * Called when a download task is deleted.
     */
    override fun onDeleted(download: Download) {
        downloadStatusListener.onDownloadCancelled(download.toDownloadInfo())
        stopNotificationServiceIfRequired.invoke()
    }

    /**
     * Called when a download task is paused.
     */
    override fun onPaused(download: Download) {
        downloadStatusListener.onDownloadPaused(download.toDownloadInfo())
    }

    /**
     * Called to report download progress.
     */
    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        downloadStatusListener.onDownloadProgress(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    /**
     * Called when a download task is queued.
     */
    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        downloadStatusListener.onDownloadProgress(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    /**
     * Called when a download task is removed.
     */
    override fun onRemoved(download: Download) {
        downloadStatusListener.onDownloadCancelled(download.toDownloadInfo())
        stopNotificationServiceIfRequired.invoke()
    }

    /**
     * Called when a download task is resumed.
     */
    override fun onResumed(download: Download) {
        downloadStatusListener.onDownloadProgress(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    /**
     * Called when a download task is started with details about download blocks.
     */
    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        downloadStatusListener.onDownloadStart(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    /**
     * Called when a download task is waiting for network connectivity.
     */
    override fun onWaitingNetwork(download: Download) {
        downloadStatusListener.onDownloadProgress(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    /**
     * Called when a download block is updated.
     */
    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {
    }

    /**
     * Called when an error occurs during the download task.
     */
    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        downloadStatusListener.onDownloadError(download.toDownloadInfo(), error)
        stopNotificationServiceIfRequired.invoke()
    }
}
