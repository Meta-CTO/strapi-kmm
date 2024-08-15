package com.metacto.strapikmm.appconfigversion

actual object AppVersionValidator {
    actual fun checkRequiredUpdate(
        appVersions: List<AppVersion>,
        applicationContext: Any?
    ): AppUpdateResult {
        TODO("Not yet implemented")
    }
}