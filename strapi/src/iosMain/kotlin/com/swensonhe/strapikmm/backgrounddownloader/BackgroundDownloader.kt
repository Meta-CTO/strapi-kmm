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

/**
 * The [BackgroundDownloader] class provides a Swift-based implementation of a background downloader that supports
 * concurrent downloads, monitoring of cellular data usage, and event callbacks.
 *
 * @param maximumNumberOfConcurrentDownloads The maximum number of concurrent downloads allowed.
 * @param allowsCellularDownloads Flag indicating whether cellular data downloads are allowed.
 * @param downloadStatusListener A listener for download status events.
 */
actual class BackgroundDownloader(
    actual val maximumNumberOfConcurrentDownloads: Int,
    actual val allowsCellularDownloads: Boolean,
    actual val downloadStatusListener: DownloadStatusListener
) {
    private val delegate = BackgroundDownloaderDelegate()
    private val logger = Logger("BackgroundDownloader")

    init {
        // Start monitoring the paths for downloads updates.
        PathMonitor.shared().startMonitoring()

        // Configure the maximum number of concurrent downloads and whether cellular downloads are allowed.
        SHBackgroundDownloader.configureMaximumNumberConcurrentDownloads(
            maximumNumberOfConcurrentDownloads.convert(),
            allowsCellularDownloads
        )

        // Set the delegate for the background downloader.
        SHBackgroundDownloader.shared().setDelegate(delegate)
    }

    /**
     * Downloads a file from the given URL asynchronously.
     *
     * @param url The URL of the file to download.
     * @return A unique identifier for the download task.
     * @throws [Throwable] if the download encounters an error.
     */
    @Throws(Throwable::class)
    actual suspend fun download(url: String): String {
        //  Run the call in a memScoped block to avoid memory leaks.
        return memScoped {
            val errorPtr: ObjCObjectVar<NSError?> = alloc()
            // get the download identifier from the background downloader.
            val identifier = SHBackgroundDownloader.shared().downloadURL(NSURL(string = url), errorPtr.ptr)
            // throw an exception if the download encounters an error.
            errorPtr.value?.let {
                throw Throwable(it.localizedDescription)
            }

            // return the download identifier if the download was started/queued/finished successfully.
            identifier ?: ""
        }
    }

    /**
     * Downloads multiple files from the given URLs asynchronously.
     *
     * @param urls A list of URLs of files to download.
     * @return A list of unique identifiers for the download tasks.
     * @throws [Throwable] if a download encounters an error.
     */
    @Throws(Throwable::class)
    actual suspend fun download(urls: List<String>): List<String> {
        return urls.map { download(it) }
    }

    /**
     * Resumes any unfinished downloads that were interrupted.
     */
    actual fun resumeUnfinishedDownloads() {
        SHBackgroundDownloader.shared().resumeUnfinishedDownloads()
    }

    /**
     * The [BackgroundDownloaderDelegate] class acts as a delegate for the [BackgroundDownloader] to handle download events
     * and report them to the [DownloadStatusListener].
     */
    private inner class BackgroundDownloaderDelegate: NSObject(), SHBackgroundDownloaderDelegateProtocol {
        /**
         * Called when an asset is successfully downloaded.
         *
         * @param id The unique identifier of the downloaded asset.
         * @param url The URL of the downloaded asset.
         */
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

        /**
         * Called when a download fails for an asset.
         *
         * @param id The unique identifier of the asset being downloaded.
         * @param url The URL of the asset.
         * @param error The error that occurred during the download.
         */
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

        /**
         * Called when the download fails to resume unfinished downloads.
         *
         * @param error The error that occurred while resuming unfinished downloads.
         */
        override fun downloaderDidFailToResumeUnfinishedDownloadsWithError(error: NSError) {
            logger.log("Did fail to resume unfinished downloads error: $error")
            downloadStatusListener.onResumeUnfinishedDownloadsError(error)
        }

        /**
         * Called when an unfinished download is successfully resumed.
         *
         * @param id The unique identifier of the resumed download.
         * @param url The URL of the resumed download.
         */
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

        /**
         * Called when the download of an asset starts.
         *
         * @param id The unique identifier of the asset being downloaded.
         * @param url The URL of the asset.
         */
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

        /**
         * Called when the download progress of an asset is updated.
         *
         * @param id The unique identifier of the asset being downloaded.
         * @param url The URL of the asset.
         * @param progress The updated download progress as a double value.
         */
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