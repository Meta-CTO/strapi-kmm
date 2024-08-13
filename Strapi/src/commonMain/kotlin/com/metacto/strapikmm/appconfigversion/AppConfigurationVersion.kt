package com.metacto.strapikmm.appconfigversion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
open class AppConfigurationVersion(
    @SerialName("applicationVersions")
    val applicationVersions: List<AppVersion>
)

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
    @SerialName("id")
    val id: Int? = null,
    @JsonNames("attributes.version", "version")
    val version: String? = null,
    @JsonNames("attributes.client", "client")
    val client: AppClient? = null,
    @JsonNames("attributes.updateType", "updateType")
    val updateType: UpdateType? = null,
)