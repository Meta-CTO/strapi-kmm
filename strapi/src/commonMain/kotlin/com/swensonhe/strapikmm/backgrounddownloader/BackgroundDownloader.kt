package com.swensonhe.strapikmm.backgrounddownloader

expect class BackgroundDownloader {
    val maximumNumberOfConcurrentDownloads: Int
    val allowsCellularDownloads: Boolean

    @Throws(Throwable::class)
    suspend fun download(url: String): Int

    @Throws(Throwable::class)
    suspend fun download(urls: List<String>): List<Int>

    fun resumeUnfinishedDownloads()

    fun setProgressListener(listener: (DownloadInfo) -> Unit)
}