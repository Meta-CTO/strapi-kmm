package com.metacto.strapikmm.errorhandling

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkError(
    @SerialName("error.status")
    val httpStatusCode: Int? = null,
    @SerialName("error.message")
    val message: String? = null,
    @SerialName("error.details.code")
    val errorCode: Int? = null
)
