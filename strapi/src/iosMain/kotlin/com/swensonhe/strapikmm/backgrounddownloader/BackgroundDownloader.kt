@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.swensonhe.strapikmm.backgrounddownloader

import com.swensonhe.strapikmm.common.downloader.backgrounddownloader.SHBackgroundDownloader
import com.swensonhe.strapikmm.common.downloader.backgrounddownloader.PathMonitor
import com.swensonhe.strapikmm.common.downloader.backgrounddownloader.SHBackgroundDownloaderDelegateProtocol
import com.swensonhe.strapikmm.util.Logger
import kotlinx.cinterop.*
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.darwin.NSInteger
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
            SHBackgroundDownloader.shared().downloadURL(NSURL(string = url), errorPtr.ptr)
            errorPtr.value?.let {
                throw Throwable(it.localizedDescription)
            }

            "0"
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
        override fun downloaderDidDownloadAssetWithIdentifier(id: String, url: NSURL) {
            logger.log("Did download asset with identifier $id at $url")
        }

        override fun downloaderDidFailToDownloadAssetWithIdentifier(
            id: String,
            errorCode: NSInteger
        ) {
            logger.log("Did fail to download asset with identifier $id errorCode: $errorCode")
        }

        override fun downloaderDidFailToResumeUnfinishedDownloadsWithError(error: NSError) {
            logger.log("Did fail to resume unfinished downloads $error")
        }

        override fun downloaderDidResumeUnfinishedDownloadWithIdentifier(id: String) {
            logger.log("Did resume unfinished download with identifier $id")
        }

        override fun downloaderDidStartDownloadingAsset(id: String) {
            logger.log("Did start downloading asset with identifier $id")
        }

        override fun downloaderDidUpdateProgress(progress: Double, id: String) {
            logger.log("Did update progress $progress for asset with identifier $id")
        }

    }
}