package com.metacto.strapikmm.appconfigversion
import platform.Foundation.NSBundle

actual object AppVersionValidator {
    actual fun checkRequiredUpdate(
        appVersions: List<AppVersion>,
        applicationContext: Any?
    ): AppUpdateResult {
        val currentAppVersion = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: "0.0.0"
        return appVersions.checkUpdateForPlatform(currentAppVersion, AppClient.IOS)
    }
}



