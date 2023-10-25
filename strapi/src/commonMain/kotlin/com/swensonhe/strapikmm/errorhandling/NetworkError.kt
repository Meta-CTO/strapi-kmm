package com.swensonhe.strapikmm.errorhandling

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A serializable data class representing a network error response.
 *
 * @param httpStatusCode The HTTP status code associated with the error.
 * @param message A human-readable error message.
 * @param errorCode An optional error code providing more details about the error.
 */

@Serializable
data class NetworkError(
    @SerialName("error.status")
    val httpStatusCode: Int? = null,
    @SerialName("error.message")
    val message: String? = null,
    @SerialName("error.details.code")
    val errorCode: Int? = null
)
