package com.metacto.strapikmm.errorhandling

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface SerializableNetworkError {
    val httpCode: Int?
    val errorMessage: String?
    val code: Int?
}

@Serializable
data class NetworkError(
    @SerialName("error.status")
    val httpStatusCode: Int? = null,
    @SerialName("error.message")
    val message: String? = null,
    @SerialName("error.details.code")
    val errorCode: Int? = null
) : SerializableNetworkError {
    override val httpCode: Int?
        get() = httpStatusCode
    override val errorMessage: String?
        get() = message
    override val code: Int?
        get() = errorCode
}
