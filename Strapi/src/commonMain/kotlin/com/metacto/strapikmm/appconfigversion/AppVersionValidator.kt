package com.metacto.strapikmm.appconfigversion

expect object AppVersionValidator {
    fun checkRequiredUpdate(appVersions: List<AppVersion>, applicationContext: Any?): UpdateType
}

internal fun AppVersion.checkUpdateVersionType(currentAppVersion: String): UpdateType {
    val currentParts = currentAppVersion.split(".").map { it.toInt() }
    val requiredParts = this.version.orEmpty().split(".").map { it.toInt() }

    for (i in 0 until maxOf(currentParts.size, requiredParts.size)) {
        val currentPart = currentParts.getOrNull(i) ?: 0
        val requiredPart = requiredParts.getOrNull(i) ?: 0

        if (currentPart < requiredPart) {
            // Current version is less than the required version then return the update type or none if not specified
            return this.updateType ?: UpdateType.NONE
        }
    }
    return UpdateType.NONE // Versions are equal, suggest no update
}