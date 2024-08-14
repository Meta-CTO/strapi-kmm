package com.metacto.strapikmm.appconfigversion
import platform.Foundation.NSBundle

actual object AppVersionValidator {
    actual fun checkRequiredUpdate(
        appVersions: List<AppVersion>,
        applicationContext: Any?
    ): UpdateType {
        val currentPublicVersion =
            appVersions.firstOrNull { appVersion ->  appVersion.client == AppClient.IOS }
                ?: return UpdateType.NONE

        val currentAppVersion =
            NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String  ?: "0.0.0"

        return currentPublicVersion.checkUpdateVersionType(currentAppVersion)
    }
}



