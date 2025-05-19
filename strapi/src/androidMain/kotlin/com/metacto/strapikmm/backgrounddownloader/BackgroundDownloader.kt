package com.metacto.strapikmm.backgrounddownloader

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.metacto.strapikmm.errorhandling.executeCatching
import com.metacto.strapikmm.util.exceptionIfActive
import com.metacto.strapikmm.util.resumeIfActive
import com.metacto.strapikmm.util.Logger
import com.metacto.strapikmm.util.applyIf
import com.tonyodev.fetch2.*
import kotlinx.coroutines.suspendCancellableCoroutine
import androidx.core.net.toUri

actual class BackgroundDownloader(
    private val context: Context,
    actual val maximumNumberOfConcurrentDownloads: Int = DEFAULT_CONCURRENT_DOWNLOADS_COUNT,
    actual val allowsCellularDownloads: Boolean = false,
    private val downloadsFolder: String = context.cacheDir.toString(),
    private val showNotifications: Boolean = true,
    private val canPauseDownloads: Boolean = true,
    private val canCancelDownloads: Boolean = true,
    actual val downloadStatusListener: DownloadStatusListener,
    private val applicationId: String
) {
    private val logger = Logger(LOG_TAG)
    private lateinit var fetch: Fetch

    init {
        validateNotificationsPermission()
        validateWritePermission()
        initFetch()
    }

    private fun validateNotificationsPermission() {
        if (showNotifications.not()) return

        if (context.checkNotificationsPermission().not()) {
            logger.log(
                "android.permission.POST_NOTIFICATIONS is not granted." +
                        "showNotifications has been reset to false."
            )
        }
    }

    private fun validateWritePermission() {
        if (downloadsFolder.canWriteToIt().not()) {
            logger.log(
                "Can't write to this path ($downloadsFolder)." +
                        "Make sure android.permission.WRITE_EXTERNAL_STORAGE permission is granted."
            )
        }
    }

    private fun initFetch() {
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
            .setNamespace(applicationId)
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

    @Throws(Throwable::class)
    actual suspend fun download(url: String): String = executeCatching {
        suspendCancellableCoroutine { cont ->
            // Create the download file path
            val filePath = getDownloadFileFullPath(url)

            // Create and config the request
            val request = Request(url, filePath).apply {
                networkType =
                    if (allowsCellularDownloads) NetworkType.ALL else NetworkType.WIFI_ONLY
                autoRetryMaxAttempts = DEFAULT_MAX_RETRY_COUNT
            }

            // Then enqueue this request
            fetch.enqueue(
                request = request,
                func = {
                    cont.resumeIfActive(it.id.toString())
                },
                func2 = {
                    cont.exceptionIfActive(
                        it.throwable ?: Throwable(it.name)
                    )
                }
            )
        }
    }

    @Throws(Throwable::class)
    actual suspend fun download(urls: List<String>): List<String> = executeCatching {
        return urls.map { download(it) }
    }

    actual fun resumeUnfinishedDownloads() {
        fetch.getDownloads { downloads ->
            // Find resumable downloads ids
            val resumableDownloads = downloads
                .filter { it.isResumable() }
                .map { it.id }

            // Then resume theme
            fetch.resume(resumableDownloads)
        }
    }

    @Throws(Throwable::class)
    actual suspend fun getDownloadState(url: String): DownloadState {
        return suspendCancellableCoroutine { cont ->
            fetch.getDownloads { downloads ->
                val download = downloads.firstOrNull { it.url == url }
                if (download == null) {
                    cont.resumeIfActive(DownloadState.NotDownloaded(url))
                    return@getDownloads
                } else {
                    val progress = download.progress
                    if (progress == 100) {
                        cont.resumeIfActive(DownloadState.Completed(download.id.toString(), url, download.file))
                    } else {
                        cont.resumeIfActive(DownloadState.Downloading(url))
                    }
                }
            }
        }
    }

    @Throws(Throwable::class)
    actual suspend fun deleteCachedFile(identifier: String): Boolean {
        val intIdentifier = identifier.toIntOrNull() ?: return false
        fetch.delete(intIdentifier)
        return true
    }

    private fun getDownloadFileFullPath(url: String): String {
        val fileName = url.toUri().pathSegments.joinToString("_")
        return "$downloadsFolder/$fileName"
    }

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

    private fun stopDownloadNotificationServiceIfRequired() {
        // Stop downloads service if required
        fetch.hasActiveDownloads(true) { hasActiveDownloads ->
            if (hasActiveDownloads) return@hasActiveDownloads
            val intent = Intent(context, BackgroundDownloaderService::class.java)
            context.stopService(intent)
        }
    }

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