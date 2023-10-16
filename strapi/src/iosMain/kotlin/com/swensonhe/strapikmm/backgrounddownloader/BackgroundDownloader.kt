@file:OptIn(ExperimentalForeignApi::class)

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
    actual val allowsCellularDownloads: Boolean
) {
    private val delegate = BackgroundDownloaderDelegate()

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
        memScoped {
            val errorPtr: ObjCObjectVar<NSError?> = alloc()
            SHBackgroundDownloader.shared().downloadURL(NSURL(string = url), errorPtr.ptr)
            errorPtr.value?.let {
                throw Throwable(it.localizedDescription)
            }
        }

        // TODO: check if you will really return a download id
        return "0"
    }

    @Throws(Throwable::class)
    actual suspend fun download(urls: List<String>): List<String> {
        memScoped {
            val errorPtr: ObjCObjectVar<NSError?> = alloc()
            SHBackgroundDownloader.shared().downloadURLs(urls.map { NSURL(string = it) }, errorPtr.ptr)
            errorPtr.value?.let {
                throw Throwable(it.localizedDescription)
            }
        }

        // TODO: check if you will really return a downloads ids
        return emptyList()
    }

    actual fun resumeUnfinishedDownloads() {
        SHBackgroundDownloader.shared().resumeUnfinishedDownloads()
    }

    private inner class BackgroundDownloaderDelegate: NSObject(), SHBackgroundDownloaderDelegateProtocol {
        override fun downloaderDidDownloadAssetWithIdentifier(id: String, url: NSURL) {
            Logger("BackgroundDownloader").log("Did download asset with identifier $id at $url")
        }

        override fun downloaderDidFailToDownloadAssetWithIdentifier(
            id: String,
            errorCode: NSInteger
        ) {
            Logger("BackgroundDownloader").log("Did fail to download asset with identifier $id errorCode: $errorCode")
        }

        override fun downloaderDidFailToResumeUnfinishedDownloadsWithError(error: NSError) {
            Logger("BackgroundDownloader").log("Did fail to resume unfinished downloads $error")
        }

        override fun downloaderDidResumeUnfinishedDownloadWithIdentifier(id: String) {
            Logger("BackgroundDownloader").log("Did resume unfinished download with identifier $id")
        }

        override fun downloaderDidStartDownloadingAsset(id: String) {
            Logger("BackgroundDownloader").log("Did start downloading asset with identifier $id")
        }

        override fun downloaderDidUpdateProgress(progress: Double, id: String) {
            Logger("BackgroundDownloader").log("Did update progress $progress for asset with identifier $id")
        }

    }

    actual fun setProgressListener(listener: (DownloadInfo) -> Unit) {
        // TODO: implement this
        // As per my discussion with Garrett, We need to implement this in the iOS side as well
    }
}