package com.metacto.strapikmm.repos

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.metacto.strapikmm.appconfigversion.AppClient
import com.metacto.strapikmm.appconfigversion.AppConfigurationVersion
import com.metacto.strapikmm.appconfigversion.AppVersion
import com.metacto.strapikmm.appconfigversion.UpdateType

actual fun List<AppVersion>.checkRequiredUpdate(
    applicationContext: Any?
): UpdateType {
    require(applicationContext is Context) {
        "applicationContext must be an Android Context and not null"
    }
    val currentPublicVersion = this.firstOrNull { appVersion -> appVersion.client == AppClient.ANDROID }
        ?: return UpdateType.NONE
    val currentAppVersion = applicationContext.getCurrentVersions()
    return checkUpdateVersionType(currentPublicVersion, currentAppVersion)
}

private fun Context.getCurrentVersions(): String {
    return packageManager.getPackageInfoCompat(packageName, 0).versionName
}

fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }