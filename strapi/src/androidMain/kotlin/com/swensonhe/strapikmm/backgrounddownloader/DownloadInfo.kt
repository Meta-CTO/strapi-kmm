package com.swensonhe.strapikmm.backgrounddownloader

/**
 * Data class representing information about a download.
 *
 * @param id The unique identifier for the download.
 * @param url The URL of the downloaded resource.
 * @param path The local file path where the download is stored.
 * @param downloaded The amount of data already downloaded in bytes.
 * @param total The total size of the resource being downloaded in bytes.
 * @param progress The progress of the download as a percentage (0-100).
 */
actual data class DownloadInfo(
    val id: String,
    val url: String,
    val path: String,
    val downloaded: Long,
    val total: Long,
    val progress: Int
)