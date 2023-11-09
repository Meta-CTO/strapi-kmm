package com.swensonhe.strapikmm.backgrounddownloader

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.swensonhe.strapikmm.util.exceptionIfActive
import com.swensonhe.strapikmm.util.resumeIfActive
import com.swensonhe.strapikmm.util.Logger
import com.swensonhe.strapikmm.util.applyIf
import com.tonyodev.fetch2.*
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * The [BackgroundDownloader] class provides a Android-based implementation of a background downloader that supports
 * concurrent downloads, monitoring of cellular data usage, and event callbacks.
 *
 * @param context The Android application context.
 * @param maximumNumberOfConcurrentDownloads The maximum number of concurrent downloads.
 * @param allowsCellularDownloads Indicates whether downloads are allowed over cellular networks.
 * @param downloadsFolder The folder path where downloaded files will be stored.
 * @param showNotifications Enables or disables download notifications.
 * @param canPauseDownloads Indicates whether downloads can be paused.
 * @param canCancelDownloads Indicates whether downloads can be canceled.
 * @param downloadStatusListener The listener for download status events.
 */
actual class BackgroundDownloader(
    private val context: Context,
    actual val maximumNumberOfConcurrentDownloads: Int = DEFAULT_CONCURRENT_DOWNLOADS_COUNT,
    actual val allowsCellularDownloads: Boolean = false,
    private val downloadsFolder: String = context.cacheDir.toString(),
    private val showNotifications: Boolean = true,
    private val canPauseDownloads: Boolean = true,
    private val canCancelDownloads: Boolean = true,
    actual val downloadStatusListener: DownloadStatusListener
) {
    private val logger = Logger(LOG_TAG)
    private lateinit var fetch: Fetch

    /**
     * Initializes the BackgroundDownloader.
     */
    init {
        // Validate notifications permissions
        validateNotificationsPermission()
        // Validate write to storage permissions
        validateWritePermission()
        // Init fetch library
        initFetch()
    }

    /*
     * Checks if the app has the required notifications permission in the current context.
     */
    private fun validateNotificationsPermission() {
        // If notifications are disabled, return
        if (showNotifications.not()) return

        // Add a log if notifications permission is not granted
        if (context.checkNotificationsPermission().not()) {
            logger.log(
                "android.permission.POST_NOTIFICATIONS is not granted." +
                        "showNotifications has been reset to false."
            )
        }
    }

    /*
     * Checks if the app has the required write to storage permission in the current context.
     */
    private fun validateWritePermission() {
        // Add a log if write to storage permission is not granted
        if (downloadsFolder.canWriteToIt().not()) {
            logger.log(
                "Can't write to this path ($downloadsFolder)." +
                        "Make sure android.permission.WRITE_EXTERNAL_STORAGE permission is granted."
            )
        }
    }

    /**
     * Initializes the Fetch download manager with the provided configurations and listeners.
     * The download manager is using Fetch library to download that's why we need to init it
     */private fun initFetch() {
        // Create notification manager
        val notificationManager = object : DefaultFetchNotificationManager(
            context = context.applicationContext,
            canPauseDownloads = canPauseDownloads,
            canCancelDownloads = canCancelDownloads
        ) {
            override fun getFetchInstanceForNamespace(namespace: String): Fetch {
                return fetch
            }
        }

        // Create fetch configs
        val configs = FetchConfiguration
            .Builder(context.applicationContext)
            .setDownloadConcurrentLimit(maximumNumberOfConcurrentDownloads)
            .setNamespace(BuildConfig.APPLICATION_ID)
            .applyIf(showNotifications) {
                setNotificationManager(notificationManager)
            }
            .build()

        // Init fetch
        fetch = Fetch.Impl.getInstance(configs).apply {
            // Add logger listener if required
            addListener(
                FetchLoggerListener(LOG_TAG)
            )

            // Add status update listener
            addListener(
                FetchStatusListener(
                    downloadStatusListener,
                    ::stopDownloadNotificationServiceIfRequired,
                    ::startDownloadNotificationServiceIfRequired
                )
            )
        }
    }

    /**
     * Initiates the download of a file from the provided URL.
     *
     * @param url The URL to download the file from.
     * @return The unique download ID associated with the download task.
     * @throws Throwable if an error occurs during the download process.
     */
    @Throws(Throwable::class)
    actual suspend fun download(url: String): String = suspendCancellableCoroutine { cont ->
        // Create the download file path
        val filePath = getDownloadFileFullPath(url)

        // Create and config the request
        val request = Request(url, filePath).apply {
            // Set the network type for the download, allowing or disallowing cellular data.
            networkType = if (allowsCellularDownloads) NetworkType.ALL else NetworkType.WIFI_ONLY
            // Define the maximum number of automatic retry attempts in case of download failures.
            autoRetryMaxAttempts = DEFAULT_MAX_RETRY_COUNT
        }

        // Then enqueue this request
        fetch.enqueue(
            request = request,
            func = {
                // When the download is enqueued successfully, resume the download with its unique ID.
                cont.resumeIfActive(it.id.toString())
            },
            func2 = {
                // If an exception occurs during download initiation, resume with the exception.
                cont.exceptionIfActive(
                    it.throwable ?: Throwable(it.name)
                )
            }
        )
    }

    /**
     * Downloads a list of URLs asynchronously.
     *
     * @param urls The list of URLs to download.
     * @return A list of download IDs corresponding to the downloaded files.
     * @throws Throwable if the download encounters an error.
     */
    @Throws(Throwable::class)
    actual suspend fun download(urls: List<String>): List<String> {
        return urls.map { download(it) }
    }

    /**
     * Resumes any unfinished downloads that can be resumed.
     * It identifies resumable downloads and resumes them.
     */
    actual fun resumeUnfinishedDownloads() {
        fetch.getDownloads { downloads ->
            // Find resumable downloads ids
            val resumableDownloads = downloads
                .filter { it.isResumable() }
                .map { it.id }

            // Resume the resumable downloads
            fetch.resume(resumableDownloads)
        }
    }

    /**
     * Returns the full file path for a given URL based on the downloads folder.
     *
     * @param url The URL for which to generate the file path.
     * @return The full file path for the downloaded file.
     */
    private fun getDownloadFileFullPath(url: String): String {
        val fileName = Uri.parse(url).lastPathSegment
        return "$downloadsFolder/$fileName"
    }

    /**
     * Retrieves information about a download based on its identifier.
     *
     * @param downloadId The unique identifier of the download (as a string).
     * @return A [DownloadInfo] object representing the download information or null if the identifier is invalid.
     * @throws CancellationException if the coroutine is cancelled.
     */
    suspend fun getDownloadInfo(downloadId: String) = suspendCancellableCoroutine { cont ->
        /*
        * iOS team need the download id as string so we returned it as string here
        * And to get the download info we use fetch.getDownload(id = downloadId) and downloadId should be Int
        * So we convert the download id to int here, or return null if the download id is not a number
         */
        if (downloadId.toIntOrNull() == null) {
            cont.resumeIfActive(null)
            return@suspendCancellableCoroutine
        }

        fetch.getDownload(id = downloadId.toInt()) { download ->
            cont.resumeIfActive(download?.toDownloadInfo())
        }
    }

    /*
     * Stops the download notification service if required.
     */
    private fun stopDownloadNotificationServiceIfRequired() {
        // Stop downloads service if required
        fetch.hasActiveDownloads(true) { hasActiveDownloads ->
            // Return if there are active downloads
            if (hasActiveDownloads) return@hasActiveDownloads
            // Stop the service
            val intent = Intent(context, BackgroundDownloaderService::class.java)
            context.stopService(intent)
        }
    }

    /*
     * Starts the download notification service if required.
     */
    private fun startDownloadNotificationServiceIfRequired() {
        // Start downloads service if required
        val intent = Intent(context, BackgroundDownloaderService::class.java)
        context.startService(intent)
    }

    companion object {
        private const val LOG_TAG = "BackgroundDownloader"
        private const val DEFAULT_MAX_RETRY_COUNT = 5
        private const val DEFAULT_CONCURRENT_DOWNLOADS_COUNT = 10
    }
}