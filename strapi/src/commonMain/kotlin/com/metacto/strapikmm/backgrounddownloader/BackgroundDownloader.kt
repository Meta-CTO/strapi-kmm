package com.metacto.strapikmm.backgrounddownloader

expect class BackgroundDownloader {
    val downloadStatusListener: DownloadStatusListener
    val maximumNumberOfConcurrentDownloads: Int
    val allowsCellularDownloads: Boolean

    @Throws(Throwable::class)
    suspend fun download(url: String): String

    @Throws(Throwable::class)
    suspend fun download(urls: List<String>): List<String>

    fun resumeUnfinishedDownloads()

    @Throws(Throwable::class)
    suspend fun getDownloadState(url: String): DownloadState

    @Throws(Throwable::class)
    suspend fun deleteCachedFile(identifier: String): Boolean

    fun updateMaximumConcurrentDownloads(newLimit: Int)
    fun updateCellularDownloadsAllowed(allowed: Boolean)
}