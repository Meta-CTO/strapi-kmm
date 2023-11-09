package com.swensonhe.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/**
 * Represents an authentication response containing user data and a JSON Web Token (JWT).
 *
 * @param user The user data associated with the authentication response.
 * @param jwt The JSON Web Token (JWT) provided upon successful authentication.
 * @param T The type of the user data. It can be a specific user model or a generic type.
 */
@Serializable
@JsExport
data class AuthResponse<T>(
    @SerialName("user")
    val user: T,
    @SerialName("jwt")
    val jwt: String?,
)