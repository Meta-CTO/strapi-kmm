package com.metacto.strapikmm.backgrounddownloader

sealed class DownloadState(val url: String) {
    class Downloading(url: String) : DownloadState(url)
    class Completed(url: String, val path: String) : DownloadState(url)
    class NotDownloaded(url: String) : DownloadState(url)
}