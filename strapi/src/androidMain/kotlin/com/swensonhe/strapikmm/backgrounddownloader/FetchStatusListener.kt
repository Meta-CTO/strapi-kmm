package com.swensonhe.strapikmm.backgrounddownloader

import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock

internal class FetchStatusListener(
    private val onDownloadingListener: (() -> Unit)? = null,
    private val onDownloadDoneListener: (() -> Unit)? = null,
    private val onDownloadProgressListener: ((DownloadInfo) -> Unit)? = null
) : FetchListener {

    override fun onAdded(download: Download) {
        onDownloadingListener?.invoke()
    }

    override fun onCancelled(download: Download) {
        onDownloadDoneListener?.invoke()
    }

    override fun onCompleted(download: Download) {
        onDownloadDoneListener?.invoke()
    }

    override fun onDeleted(download: Download) {
        onDownloadDoneListener?.invoke()
    }

    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        onDownloadDoneListener?.invoke()
    }

    override fun onPaused(download: Download) {
    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        onDownloadProgressListener?.invoke(download.toDownloadInfo())
    }

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        onDownloadingListener?.invoke()
    }

    override fun onRemoved(download: Download) {
        onDownloadDoneListener?.invoke()
    }

    override fun onResumed(download: Download) {
        onDownloadingListener?.invoke()
    }

    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        onDownloadingListener?.invoke()
    }

    override fun onWaitingNetwork(download: Download) {
        onDownloadingListener?.invoke()
    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {
    }
}