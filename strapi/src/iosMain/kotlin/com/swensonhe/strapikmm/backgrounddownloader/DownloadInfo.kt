package com.swensonhe.strapikmm.backgrounddownloader

import platform.Foundation.NSURL

/**
 * The [DownloadInfo] data class represents information about a download operation, including its unique identifier,
 * download URL, progress, and completion status.
 *
 * @property id The unique identifier associated with the download operation.
 * @property url The URL of the downloaded asset, which can be nullable.
 * @property progress The download progress as a double value, where 0.0 represents no progress and 1.0 indicates
 * that the download is complete.
 * @property isDownloadComplete A boolean flag indicating whether the download operation is complete (true) or ongoing (false).
 */
actual data class DownloadInfo(
    val id: String,
    val url: NSURL?,
    val progress: Double,
    val isDownloadComplete: Boolean
)