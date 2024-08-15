package com.metacto.strapikmm.appconfigversion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

interface AppConfigurationVersion {
    val applicationVersions: List<AppVersion>
}

@Serializable
enum class AppClient(val client: String) {
    @SerialName("ios")
    IOS("ios"),

    @SerialName("android")
    ANDROID("android");
}

@Serializable
enum class UpdateType(val type: String) {
    @SerialName("required")
    REQUIRED("required"),

    @SerialName("optional")
    OPTIONAL("optional"),

    @SerialName("none")
    NONE("none");
}

@Serializable
data class AppVersion(
    @JsonNames("attributes.message", "message")
    val message: String? = null,
    @JsonNames("attributes.version", "version")
    val version: String? = null,
    @JsonNames("attributes.client", "client")
    val client: AppClient? = null,
    @JsonNames("attributes.updateType", "updateType")
    val updateType: UpdateType? = null,
)

@Serializable
data class AppUpdateResult(
    @SerialName("updateType")
    val updateType: UpdateType,
    @SerialName("message")
    val message: String
)