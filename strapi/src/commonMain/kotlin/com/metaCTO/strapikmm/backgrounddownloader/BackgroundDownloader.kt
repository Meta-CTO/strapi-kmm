package com.metaCTO.strapikmm.backgrounddownloader

expect class BackgroundDownloader {
    val downloadStatusListener: DownloadStatusListener
    val maximumNumberOfConcurrentDownloads: Int
    val allowsCellularDownloads: Boolean

    @Throws(Throwable::class)
    suspend fun download(url: String): String

    @Throws(Throwable::class)
    suspend fun download(urls: List<String>): List<String>

    fun resumeUnfinishedDownloads()
}