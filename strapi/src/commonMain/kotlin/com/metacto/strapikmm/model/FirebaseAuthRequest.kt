package com.metacto.strapikmm.model

import com.metacto.strapikmm.auth.ProfileMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FirebaseAuthRequest(
    @SerialName("idToken")
    val idToken: String?,
    @SerialName("profileMetaData")
    val profile: ProfileMetadata?
)
