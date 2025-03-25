package com.metacto.strapikmm.backgrounddownloader

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

    actual suspend fun getDownloadState(url: String): DownloadState {
        TODO("Not yet implemented")
    }

    actual suspend fun deleteCachedFile(identifier: String): Boolean {
        TODO("Not yet implemented")
    }
}