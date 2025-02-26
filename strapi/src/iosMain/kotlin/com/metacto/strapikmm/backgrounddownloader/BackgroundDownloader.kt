@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.metacto.strapikmm.backgrounddownloader

import com.metacto.strapikmm.common.downloader.backgrounddownloader.SHBackgroundDownloader
import com.metacto.strapikmm.common.downloader.backgrounddownloader.PathMonitor
import com.metacto.strapikmm.common.downloader.backgrounddownloader.SHBackgroundDownloaderDelegateProtocol
import com.metacto.strapikmm.errorhandling.ErrorMapper
import com.metacto.strapikmm.errorhandling.executeCatching
import com.metacto.strapikmm.util.Logger
import kotlinx.cinterop.*
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.darwin.NSObject

actual class BackgroundDownloader(
    actual val maximumNumberOfConcurrentDownloads: Int,
    actual val allowsCellularDownloads: Boolean,
    actual val downloadStatusListener: DownloadStatusListener
) {
    private val delegate = BackgroundDownloaderDelegate()
    private val logger = Logger("BackgroundDownloader")

    init {
        PathMonitor.shared().startMonitoring()

        SHBackgroundDownloader.configureMaximumNumberConcurrentDownloads(
            maximumNumberOfConcurrentDownloads.convert(),
            allowsCellularDownloads
        )

        SHBackgroundDownloader.shared().setDelegate(delegate)
    }

    @Throws(Throwable::class)
    actual suspend fun download(url: String): String = executeCatching {
        return memScoped {
            val errorPtr: ObjCObjectVar<NSError?> = alloc()
            val identifier =
                SHBackgroundDownloader.shared().downloadURL(NSURL(string = url), errorPtr.ptr)
            errorPtr.value?.let {
                throw ErrorMapper.mapThrowable(it)
            }

            identifier ?: ""
        }
    }

    @Throws(Throwable::class)
    actual suspend fun download(urls: List<String>): List<String> = executeCatching {
        return urls.map { download(it) }
    }

    actual fun resumeUnfinishedDownloads() {
        SHBackgroundDownloader.shared().resumeUnfinishedDownloads()
    }

    @Throws(Throwable::class)
    actual suspend fun getDownloadState(url: String): DownloadState = executeCatching {
        return memScoped {
            val errorPtr: ObjCObjectVar<NSError?> = alloc()
            val downloadedObject = SHBackgroundDownloader.shared().checkDownloadStatusWithURL(NSURL(string = url), errorPtr.ptr)

            errorPtr.value?.let {
                return@executeCatching DownloadState.NotDownloaded(url)
            }

            if (downloadedObject == null) {
                return@executeCatching DownloadState.NotDownloaded(url)
            }

            val downloadEndTime = downloadedObject.downloadEndTime()
            return@executeCatching if (downloadEndTime != null && downloadedObject.url() != null) {
                DownloadState.Completed(url, downloadedObject.url()?.absoluteString.orEmpty())
            } else {
                DownloadState.Downloading(url)
            }
        }
    }

    private inner class BackgroundDownloaderDelegate : NSObject(),
        SHBackgroundDownloaderDelegateProtocol {
        override fun downloaderDidDownloadAssetWithIdentifier(
            id: String,
            url: NSURL?,
            downloadURL: NSURL?
        ) {
            logger.log("Did download asset with identifier: $id url: $url")
            downloadStatusListener.onDownloadDone(
                DownloadInfo(
                    id = id,
                    cacheURL = url,
                    downloadURL = downloadURL,
                    progress = 1.0,
                    isDownloadComplete = true
                )
            )
        }

        override fun downloaderDidFailToDownloadAssetWithIdentifier(
            id: String,
            url: NSURL?,
            downloadURL: NSURL?,
            error: NSError
        ) {
            logger.log("Did fail to download asset with identifier: $id url: $url error: $error")
            downloadStatusListener.onDownloadError(
                downloadInfo = DownloadInfo(
                    id = id,
                    cacheURL = url,
                    downloadURL = downloadURL,
                    progress = 0.0,
                    isDownloadComplete = false
                ),
                error = error
            )
        }

        override fun downloaderDidFailToResumeUnfinishedDownloadsWithError(error: NSError) {
            logger.log("Did fail to resume unfinished downloads error: $error")
            downloadStatusListener.onResumeUnfinishedDownloadsError(error)
        }

        override fun downloaderDidResumeUnfinishedDownloadWithIdentifier(
            id: String,
            url: NSURL?,
            downloadURL: NSURL?
        ) {
            logger.log("Did resume unfinished download with identifier: $id url: $url")
            downloadStatusListener.onUnfinishedDownloadStart(
                DownloadInfo(
                    id = id,
                    cacheURL = url,
                    downloadURL = downloadURL,
                    progress = 0.0,
                    isDownloadComplete = false
                )
            )
        }

        override fun downloaderDidStartDownloadingAsset(
            id: String,
            url: NSURL?,
            downloadURL: NSURL?
        ) {
            logger.log("Did start downloading asset with identifier: $id url: $url")
            downloadStatusListener.onDownloadStart(
                DownloadInfo(
                    id = id,
                    cacheURL = url,
                    downloadURL = downloadURL,
                    progress = 0.0,
                    isDownloadComplete = false
                )
            )
        }

        override fun downloaderDidUpdateProgress(
            id: String,
            url: NSURL?,
            downloadURL: NSURL?,
            progress: Double
        ) {
            logger.log("Did update progress identifier: $id url: $url progress: $progress")
            downloadStatusListener.onDownloadProgress(
                DownloadInfo(
                    id = id,
                    cacheURL = url,
                    downloadURL = downloadURL,
                    progress = progress,
                    isDownloadComplete = false
                )
            )
        }
    }
}
