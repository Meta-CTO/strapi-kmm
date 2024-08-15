package com.metacto.strapikmm.appconfigversion

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

actual object AppVersionValidator {
    actual fun checkRequiredUpdate(
        appVersions: List<AppVersion>,
        applicationContext: Any?
    ): AppUpdateResult {
        require(applicationContext is Context) {
            "applicationContext must be an Android Context and not null"
        }
        val currentPublicVersion =
            appVersions.firstOrNull { appVersion -> appVersion.client == AppClient.ANDROID }
                ?: return AppUpdateResult(
                    updateType = UpdateType.NONE,
                    message = "No version found for Android"
                )
        val currentAppVersion = applicationContext.getCurrentVersion()
        return currentPublicVersion.checkUpdateVersionType(currentAppVersion)
    }
}

private fun Context.getCurrentVersion(): String {
    return packageManager.getPackageInfoCompat(packageName, 0).versionName
}

private fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }