package com.swensonhe.strapikmm.backgrounddownloader

actual class BackgroundDownloader(
    actual val maximumNumberOfConcurrentDownloads: Int,
    actual val allowsCellularDownloads: Boolean
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

    actual fun setProgressListener(listener: (DownloadInfo) -> Unit) {
        TODO("Not yet implemented")
    }
}