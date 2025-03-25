package com.metacto.strapikmm.backgrounddownloader

sealed class DownloadState(val url: String) {
    class Downloading(url: String) : DownloadState(url)
    class Completed(identifier: String, url: String, val path: String) : DownloadState(url)
    class NotDownloaded(url: String) : DownloadState(url)
}