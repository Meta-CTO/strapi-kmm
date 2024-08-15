package com.metacto.strapikmm.appconfigversion

expect object AppVersionValidator {
    fun checkRequiredUpdate(appVersions: List<AppVersion>, applicationContext: Any?): AppUpdateResult
}

internal fun AppVersion.checkUpdateVersionType(currentAppVersion: String): AppUpdateResult {
    val currentParts = currentAppVersion.split(".").map { it.toInt() }
    val requiredParts = this.version.orEmpty().split(".").map { it.toInt() }
    var suggestedUpdate: UpdateType? = UpdateType.NONE

    for (i in 0 until maxOf(currentParts.size, requiredParts.size)) {
        val currentPart = currentParts.getOrNull(i) ?: 0
        val requiredPart = requiredParts.getOrNull(i) ?: 0

        if (currentPart < requiredPart) {
            // Current version is less than the required version then return the update type or none if not specified
            suggestedUpdate = this.updateType ?: UpdateType.NONE
        }
    }
    return AppUpdateResult(suggestedUpdate ?: UpdateType.NONE, this.message.orEmpty())
}