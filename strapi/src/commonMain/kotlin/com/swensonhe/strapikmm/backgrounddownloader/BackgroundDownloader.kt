package com.swensonhe.strapikmm.backgrounddownloader

/**
 * An expect class representing a background downloader for downloading content from URLs.
 * Platform-specific implementations of this class should be provided for each platform (e.g., Web, Android, iOS).
 */
expect class BackgroundDownloader {
    /**
     * The listener for download status updates, which allows tracking the progress of ongoing downloads.
     */
    val downloadStatusListener: DownloadStatusListener

    /**
     * The maximum number of concurrent downloads allowed by the downloader.
     */
    val maximumNumberOfConcurrentDownloads: Int

    /**
     * Indicates whether cellular network connections are allowed for downloads.
     */
    val allowsCellularDownloads: Boolean

    /**
     * Downloads content from the specified URL asynchronously.
     *
     * @param url The URL from which to download content.
     * @return A string representing the download process id to be used for tracking the download status.
     *
     * @throws Throwable if there is an error during the download process.
     */
    @Throws(Throwable::class)
    suspend fun download(url: String): String

    /**
     * Downloads content from a list of URLs asynchronously.
     *
     * @param urls The list of URLs from which to download content.
     * @return A list of strings representing the download process ids to be used for tracking the download status.
     *
     * @throws Throwable if there is an error during any of the download processes.
     */
    @Throws(Throwable::class)
    suspend fun download(urls: List<String>): List<String>

    /**
     * Resumes any unfinished downloads that were interrupted or paused.
     * This function is useful for continuing downloads after a network interruption or app restart.
     */
    fun resumeUnfinishedDownloads()
}
