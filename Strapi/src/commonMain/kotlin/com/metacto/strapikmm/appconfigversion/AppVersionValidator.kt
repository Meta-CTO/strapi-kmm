package com.metacto.strapikmm.appconfigversion

expect object AppVersionValidator {
    fun checkRequiredUpdate(
        appVersions: List<AppVersion>,
        applicationContext: Any?
    ): AppUpdateResult
}

fun AppVersion.checkUpdateVersionType(currentAppVersion: String): UpdateType {
    // Filter out non-numeric characters and split by dot to get version parts for current version
    val currentParts =
        currentAppVersion.filter { it.isDigit() || it == '.' }.split(".").map { it.toInt() }

    // Filter out non-numeric characters and split by dot to get version parts for required version
    val requiredParts =
        this.version.orEmpty().filter { it.isDigit() || it == '.' }.split(".").map { it.toInt() }

    // Compare the current version parts with the required version parts
    for (i in 0 until maxOf(currentParts.size, requiredParts.size)) {
        // Get the current and required part or default to 0
        val currentPart = currentParts.getOrNull(i) ?: 0
        // Get the required part or default to 0
        val requiredPart = requiredParts.getOrNull(i) ?: 0

        // If the current part is less than the required part, suggest the update type
        if (currentPart < requiredPart) {
            // Return the update type or none if not specified
            return this.updateType ?: UpdateType.NONE
        }
    }
    // Return none if not specified
    return UpdateType.NONE
}

fun List<AppVersion>.checkUpdateForPlatform(
    currentAppVersion: String,
    client: AppClient
): AppUpdateResult {
    val appVersions = this
    val filteredCurrentVersion = currentAppVersion
        .filter { it.isDigit() || it == '.' } // Remove non-digit characters
        .takeIf { it.isNotEmpty() } ?: "0.0.0" // Fallback to "0.0.0" if empty

    val filteredVersions = appVersions
        .filter { it.client == client } // Filter for the specified platform
        .filter { it.checkUpdateVersionType(filteredCurrentVersion) != UpdateType.NONE } // Filter out lower versions

    val requiredUpdate = filteredVersions
        .filter { it.updateType == UpdateType.REQUIRED }
        .maxByOrNull { it.version.orEmpty().filter { it.isDigit() || it == '.' } }

    if (requiredUpdate != null) {
        return AppUpdateResult(UpdateType.REQUIRED, requiredUpdate.message.orEmpty())
    }

    val optionalUpdate = filteredVersions
        .filter { it.updateType == UpdateType.OPTIONAL }
        .maxByOrNull { it.version.orEmpty().filter { it.isDigit() || it == '.' } }

    if (optionalUpdate != null) {
        return AppUpdateResult(UpdateType.OPTIONAL, optionalUpdate.message.orEmpty())
    }

    return AppUpdateResult(UpdateType.NONE, "No updates required")
}
