package com.swensonhe.strapikmm.backgrounddownloader

import com.swensonhe.strapikmm.util.Logger
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock

/**
 * An internal class that logs download events and status changes for debugging and monitoring purposes.
 *
 * @param logTag The tag to use for logging.
 */
internal class FetchLoggerListener(logTag: String) : FetchListener {
    private val logger = Logger(logTag)

    /**
     * Called when a download task is added to the queue.
     */
    override fun onAdded(download: Download) {
        val msg = buildString {
            append("onAdded:--------------------------------\n")
            append("url: ${download.url}\n")
            append("downloadPath: ${download.file}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    /**
     * Called when a download task is cancelled.
     */
    override fun onCancelled(download: Download) {
        val msg = buildString {
            append("onCancelled:----------------------------\n")
            append("url: ${download.url}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    /**
     * Called when a download task is completed.
     */
    override fun onCompleted(download: Download) {
        val msg = buildString {
            append("onCompleted:----------------------------\n")
            append("url: ${download.url}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    /**
     * Called when a download task is deleted.
     */
    override fun onDeleted(download: Download) {
        val msg = buildString {
            append("onDeleted:------------------------------\n")
            append("url: ${download.url}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    /**
     * Called when an error occurs during the download task.
     */
    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        val msg = buildString {
            append("onError:-----------------\n")
            append("url: ${download.url}\n")
            append("error: ${error}\n")
            append("throwable: ${throwable?.message}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    /**
     * Called when a download task is paused.
     */
    override fun onPaused(download: Download) {
        val msg = buildString {
            append("onPaused:-----------------\n")
            append("url: ${download.url}\n")
            append("progress: ${download.progress}%\n")
            append("downloaded: ${download.downloaded.div(1024)}kb\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    /**
     * Called to report download progress.
     */
    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        val msg = buildString {
            append("onProgress:-----------------\n")
            append("url: ${download.url}\n")
            append("progress: ${download.progress}%\n")
            append("downloaded: ${download.downloaded.div(1024)}kb\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    /**
     * Called when a download task is queued.
     */
    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        val msg = buildString {
            append("onQueued:-----------------\n")
            append("url: ${download.url}\n")
            append("waitingOnNetwork: $waitingOnNetwork\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    /**
     * Called when a download task is removed.
     */
    override fun onRemoved(download: Download) {
        val msg = buildString {
            append("onRemoved:-----------------\n")
            append("url: ${download.url}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    /**
     * Called when a download task is resumed.
     */
    override fun onResumed(download: Download) {
        val msg = buildString {
            append("onResumed:-----------------\n")
            append("url: ${download.url}\n")
            append("progress: ${download.progress}%\n")
            append("downloaded: ${download.downloaded.div(1024)}kb\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    /**
     * Called when a download task is started with details about download blocks.
     */
    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        val msg = buildString {
            append("onStarted:-----------------\n")
            append("url: ${download.url}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    /**
     * Called when a download task is waiting for network connectivity.
     */
    override fun onWaitingNetwork(download: Download) {
        val msg = buildString {
            append("onWaitingNetwork:-----------------\n")
            append("url: ${download.url}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
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
}
