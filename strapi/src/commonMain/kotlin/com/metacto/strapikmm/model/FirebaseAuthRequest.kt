package com.metacto.strapikmm.model

import com.metacto.kmm.auth.common.ProfileMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FirebaseAuthRequest(
    @SerialName("idToken")
    val idToken: String?,
    @SerialName("profileMetaData")
    val profile: ProfileMetadata?
)
