package com.swensonhe.strapikmm.backgrounddownloader

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Status
import java.io.File

internal fun Download.toDownloadInfo() = DownloadInfo(
    id = id,
    url = url,
    path = file,
    downloaded = downloaded,
    total = total,
    progress = progress
)

internal fun Context.checkNotificationsPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        return true
    }
}

internal fun String.canWriteToIt(): Boolean {
    return try {
        File(this).canWrite()
    } catch (e: Throwable) {
        false
    }
}

internal fun Download.isResumable(): Boolean {
    return status == Status.NONE
            || status == Status.QUEUED
            || status == Status.DOWNLOADING
            || status == Status.ADDED
}