package com.swensonhe.strapikmm.backgrounddownloader

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Status
import java.io.File

/**
 * Converts a [Download] object into a [DownloadInfo] for easy retrieval of download information.
 *
 * @return A [DownloadInfo] object containing download details.
 */
internal fun Download.toDownloadInfo() = DownloadInfo(
    id = id.toString(),
    url = url,
    path = file,
    downloaded = downloaded,
    total = total,
    progress = progress
)

/**
 * Checks if the app has the required notifications permission in the current context.
 *
 * @return `true` if the app has the notifications permission, `false` otherwise.
 */
internal fun Context.checkNotificationsPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        return true
    }
}

/**
 * Checks if a given path is writable.
 *
 * @return `true` if the path is writable, `false` otherwise.
 */
internal fun String.canWriteToIt(): Boolean {
    return try {
        File(this).canWrite()
    } catch (e: Throwable) {
        false
    }
}

/**
 * Checks if a download is in a resumable state.
 *
 * @return `true` if the download is in a resumable state, `false` otherwise.
 */
internal fun Download.isResumable(): Boolean {
    return status == Status.NONE
            || status == Status.QUEUED
            || status == Status.DOWNLOADING
            || status == Status.ADDED
}