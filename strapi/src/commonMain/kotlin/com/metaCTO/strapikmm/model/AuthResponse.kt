package com.metaCTO.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
data class AuthResponse<T>(
    @SerialName("user")
    val user: T,
    @SerialName("jwt")
    val jwt: String?,
)