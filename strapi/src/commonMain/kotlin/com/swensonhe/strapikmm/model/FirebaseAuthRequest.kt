package com.swensonhe.strapikmm.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a request to authenticate with Firebase using an ID token.
 *
 * @param idToken The ID token obtained from the Firebase Authentication SDK/Service.
 */
@Serializable
class FirebaseAuthRequest(
    @SerialName("idToken")
    val idToken: String?
)
