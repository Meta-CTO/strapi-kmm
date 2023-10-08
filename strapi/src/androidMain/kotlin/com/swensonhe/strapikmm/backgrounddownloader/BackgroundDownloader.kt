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


actual class BackgroundDownloader(
    private val context: Context,
    actual val maximumNumberOfConcurrentDownloads: Int = DEFAULT_CONCURRENT_DOWNLOADS_COUNT,
    actual val allowsCellularDownloads: Boolean = false,
    private val downloadsFolder: String = context.cacheDir.toString(),
    private val showNotifications: Boolean = true,
    private val canPauseDownloads: Boolean = true,
    private val canCancelDownloads: Boolean = true
) {
    private val logger = Logger(LOG_TAG)
    private lateinit var fetch: Fetch
    private var progressListener: ((DownloadInfo) -> Unit)? = null
    private var onDownloadError: ((Download, Error) -> Unit)? = null

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
                    onDownloadingListener = ::onDownloading,
                    onDownloadDoneListener = ::onDownloadDone,
                    onDownloadProgressListener = ::onDownloadProgress,
                    onDownloadErrorListener = ::onDownloadError
                )
            )
        }
    }

    @Throws(Throwable::class)
    actual suspend fun download(url: String): Int = suspendCancellableCoroutine { cont ->
        // Create the download file path
        val filePath = getDownloadFileFullPath(url)

        // Create and config the request
        val request = Request(url, filePath).apply {
            networkType = if (allowsCellularDownloads) NetworkType.ALL else NetworkType.WIFI_ONLY
            autoRetryMaxAttempts = DEFAULT_MAX_RETRY_COUNT
        }

        // Then enqueue this request
        fetch.enqueue(
            request = request,
            func = {
                cont.resumeIfActive(it.id)
            },
            func2 = {
                cont.exceptionIfActive(
                    it.throwable ?: Throwable(it.name)
                )
            }
        )
    }

    @Throws(Throwable::class)
    actual suspend fun download(urls: List<String>): List<Int> {
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

    private fun getDownloadFileFullPath(url: String): String {
        val fileName = Uri.parse(url).lastPathSegment
        return "$downloadsFolder/$fileName"
    }

    suspend fun getDownloadInfo(downloadId: Int) = suspendCancellableCoroutine { cont ->
        fetch.getDownload(id = downloadId) { download ->
            cont.resumeIfActive(download?.toDownloadInfo())
        }
    }

    actual fun setProgressListener(listener: (DownloadInfo) -> Unit) {
        progressListener = listener
    }

    fun setDownloadErrorListener(listener: (Download, Error) -> Unit) {
        onDownloadError = listener
        // Stop downloads service if required
        stopNotificationServiceIfRequired()
    }

    private fun stopNotificationServiceIfRequired() {
        // Stop downloads service if required
        fetch.hasActiveDownloads(true) { hasActiveDownloads ->
            if (hasActiveDownloads) return@hasActiveDownloads
            val intent = Intent(context, BackgroundDownloaderService::class.java)
            context.stopService(intent)
        }
    }

    private fun onDownloading() {
        // Start downloads service if required
        val intent = Intent(context, BackgroundDownloaderService::class.java)
        context.startService(intent)
    }

    private fun onDownloadDone() {
        // Stop downloads service if required
        stopNotificationServiceIfRequired()
    }

    private fun onDownloadProgress(download: DownloadInfo) {
        progressListener?.invoke(download)
    }

    private fun onDownloadError(download: Download, error: Error) {
        onDownloadError?.invoke(download, error)
    }

    companion object {
        private const val LOG_TAG = "BackgroundDownloader"
        private const val DEFAULT_MAX_RETRY_COUNT = 5
        private const val DEFAULT_CONCURRENT_DOWNLOADS_COUNT = 10
    }
}