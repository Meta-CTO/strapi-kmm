package com.metaCTO.strapikmm.backgrounddownloader

import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock

internal class FetchStatusListener(
    private val downloadStatusListener: DownloadStatusListener,
    private val stopNotificationServiceIfRequired: () -> Unit,
    private val startDownloadServiceIfRequired: () -> Unit
) : FetchListener {

    override fun onAdded(download: Download) {
        downloadStatusListener.onDownloadStart(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    override fun onCancelled(download: Download) {
        downloadStatusListener.onDownloadCancelled(download.toDownloadInfo())
        stopNotificationServiceIfRequired.invoke()
    }

    override fun onCompleted(download: Download) {
        downloadStatusListener.onDownloadDone(download.toDownloadInfo())
        stopNotificationServiceIfRequired.invoke()
    }

    override fun onDeleted(download: Download) {
        downloadStatusListener.onDownloadCancelled(download.toDownloadInfo())
        stopNotificationServiceIfRequired.invoke()
    }

    override fun onPaused(download: Download) {
        downloadStatusListener.onDownloadPaused(download.toDownloadInfo())
    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        downloadStatusListener.onDownloadProgress(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        downloadStatusListener.onDownloadProgress(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    override fun onRemoved(download: Download) {
        downloadStatusListener.onDownloadCancelled(download.toDownloadInfo())
        stopNotificationServiceIfRequired.invoke()
    }

    override fun onResumed(download: Download) {
        downloadStatusListener.onDownloadProgress(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        downloadStatusListener.onDownloadStart(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    override fun onWaitingNetwork(download: Download) {
        downloadStatusListener.onDownloadProgress(download.toDownloadInfo())
        startDownloadServiceIfRequired.invoke()
    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {
    }

    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        downloadStatusListener.onDownloadError(download.toDownloadInfo(), error)
        stopNotificationServiceIfRequired.invoke()
    }
}