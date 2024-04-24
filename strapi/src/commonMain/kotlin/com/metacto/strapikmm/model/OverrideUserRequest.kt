package com.metacto.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class OverrideUserRequest(
    @SerialName("overrideUserId")
    val overrideUserId: Int
)