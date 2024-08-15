package com.metacto.strapikmm.appconfigversion
import platform.Foundation.NSBundle

actual object AppVersionValidator {
    actual fun checkRequiredUpdate(
        appVersions: List<AppVersion>,
        applicationContext: Any?
    ): AppUpdateResult {
        val currentPublicVersion =
            appVersions.firstOrNull { appVersion ->  appVersion.client == AppClient.IOS }
                ?: return AppUpdateResult(
                    updateType = UpdateType.NONE,
                    message = "No version found for iOS"
                )

        val currentAppVersion =
            NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String  ?: "0.0.0"

        return currentPublicVersion.checkUpdateVersionType(currentAppVersion)
    }
}



