@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.metacto.strapikmm.backgrounddownloader

import com.metacto.strapikmm.common.downloader.backgrounddownloader.SHBackgroundDownloader
import com.metacto.strapikmm.common.downloader.backgrounddownloader.PathMonitor
import com.metacto.strapikmm.common.downloader.backgrounddownloader.SHBackgroundDownloaderDelegateProtocol
import com.metacto.strapikmm.errorhandling.NetworkErrorMapper
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
    actual suspend fun download(url: String): String {
        return memScoped {
            val errorPtr: ObjCObjectVar<NSError?> = alloc()
            val identifier = SHBackgroundDownloader.shared().downloadURL(NSURL(string = url), errorPtr.ptr)
            errorPtr.value?.let {
                throw NetworkErrorMapper.mapThrowable(it)
            }

            identifier ?: ""
        }
    }

    @Throws(Throwable::class)
    actual suspend fun download(urls: List<String>): List<String> {
        return urls.map { download(it) }
    }

    actual fun resumeUnfinishedDownloads() {
        SHBackgroundDownloader.shared().resumeUnfinishedDownloads()
    }

    private inner class BackgroundDownloaderDelegate: NSObject(), SHBackgroundDownloaderDelegateProtocol {
        override fun downloaderDidDownloadAssetWithIdentifier(id: String, url: NSURL?) {
            logger.log("Did download asset with identifier: $id url: $url")
            downloadStatusListener.onDownloadDone(
                DownloadInfo(
                    id = id,
                    url = url,
                    progress = 1.0,
                    isDownloadComplete = true
                )
            )
        }

        override fun downloaderDidFailToDownloadAssetWithIdentifier(
            id: String,
            url: NSURL?,
            error: NSError
        ) {
            logger.log("Did fail to download asset with identifier: $id url: $url error: $error")
            downloadStatusListener.onDownloadError(
                downloadInfo = DownloadInfo(
                    id = id,
                    url = url,
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

        override fun downloaderDidResumeUnfinishedDownloadWithIdentifier(id: String, url: NSURL?) {
            logger.log("Did resume unfinished download with identifier: $id url: $url")
            downloadStatusListener.onUnfinishedDownloadStart(
                DownloadInfo(
                    id = id,
                    url = url,
                    progress = 0.0,
                    isDownloadComplete = false
                )
            )
        }

        override fun downloaderDidStartDownloadingAsset(id: String, url: NSURL?) {
            logger.log("Did start downloading asset with identifier: $id url: $url")
            downloadStatusListener.onDownloadStart(
                DownloadInfo(
                    id = id,
                    url = url,
                    progress = 0.0,
                    isDownloadComplete = false
                )
            )
        }

        override fun downloaderDidUpdateProgress(id: String, url: NSURL?, progress: Double) {
            logger.log("Did update progress identifier: $id url: $url progress: $progress")
            downloadStatusListener.onDownloadProgress(
                DownloadInfo(
                    id = id,
                    url = url,
                    progress = progress,
                    isDownloadComplete = false
                )
            )
        }
    }
}