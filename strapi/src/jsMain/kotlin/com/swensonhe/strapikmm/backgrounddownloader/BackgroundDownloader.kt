package com.swensonhe.strapikmm.backgrounddownloader

/**
 *  Provide download Service for web
 * We didn't implement any logic for Web (For now), so we just return a no-op implementation here.
 * ** Any PRs to implement it for Web are welcome! **
 */
actual class BackgroundDownloader(
    actual val maximumNumberOfConcurrentDownloads: Int,
    actual val allowsCellularDownloads: Boolean,
    actual val downloadStatusListener: DownloadStatusListener
) {

    actual suspend fun download(url: String): String {
        TODO("Not yet implemented")
    }

    actual suspend fun download(urls: List<String>): List<String> {
        TODO("Not yet implemented")
    }

    actual fun resumeUnfinishedDownloads() {
        TODO("Not yet implemented")
    }
}