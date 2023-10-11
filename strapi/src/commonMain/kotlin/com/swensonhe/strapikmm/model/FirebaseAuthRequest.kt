package com.swensonhe.strapikmm.model

import com.swensonhe.strapikmm.auth.ProfileMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FirebaseAuthRequest(
    @SerialName("idToken")
    val idToken: String?,
    @SerialName("profile")
    val profile: ProfileMetadata?
)
