package com.metaCTO.strapikmm.model

import com.metaCTO.strapikmm.auth.ProfileMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FirebaseAuthRequest(
    @SerialName("idToken")
    val idToken: String?,
    @SerialName("profileMetaData")
    val profile: ProfileMetadata?
)
