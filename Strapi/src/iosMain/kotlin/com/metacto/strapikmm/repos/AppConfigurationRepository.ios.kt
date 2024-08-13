package com.metacto.strapikmm.repos

import com.metacto.strapikmm.appconfigversion.AppClient
import com.metacto.strapikmm.appconfigversion.AppConfigurationVersion
import com.metacto.strapikmm.appconfigversion.AppVersion
import com.metacto.strapikmm.appconfigversion.UpdateType
import platform.Foundation.NSBundle

actual fun List<AppVersion>.checkRequiredUpdate(applicationContext: Any?): UpdateType {
    val currentPublicVersion =
        this.firstOrNull { appVersion ->  appVersion.client == AppClient.IOS }
            ?: return UpdateType.NONE

    val currentAppVersion =
        NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String  ?: "0.0.0"

    return checkUpdateVersionType(currentPublicVersion, currentAppVersion)
}