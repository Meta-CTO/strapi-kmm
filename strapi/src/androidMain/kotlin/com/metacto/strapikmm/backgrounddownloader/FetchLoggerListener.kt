package com.metacto.strapikmm.backgrounddownloader

import com.metacto.strapikmm.util.Logger
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock

internal class FetchLoggerListener(logTag: String) : FetchListener {
    private val logger = Logger(logTag)

    override fun onAdded(download: Download) {
        val msg = buildString {
            append("onAdded:--------------------------------\n")
            append("url: ${download.url}\n")
            append("downloadPath: ${download.file}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    override fun onCancelled(download: Download) {
        val msg = buildString {
            append("onCancelled:----------------------------\n")
            append("url: ${download.url}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    override fun onCompleted(download: Download) {
        val msg = buildString {
            append("onCompleted:----------------------------\n")
            append("url: ${download.url}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    override fun onDeleted(download: Download) {
        val msg = buildString {
            append("onDeleted:------------------------------\n")
            append("url: ${download.url}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

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

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        val msg = buildString {
            append("onQueued:-----------------\n")
            append("url: ${download.url}\n")
            append("waitingOnNetwork: $waitingOnNetwork\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    override fun onRemoved(download: Download) {
        val msg = buildString {
            append("onRemoved:-----------------\n")
            append("url: ${download.url}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

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

    override fun onWaitingNetwork(download: Download) {
        val msg = buildString {
            append("onWaitingNetwork:-----------------\n")
            append("url: ${download.url}\n")
            append("----------------------------------------")
        }
        logger.log(msg)
    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {
    }
}