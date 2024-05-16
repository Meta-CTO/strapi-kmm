package com.metacto.strapikmm.errorhandling

import com.metacto.strapikmm.datasource.network.services.strapi.JsonFlatter
import com.metacto.strapikmm.datasource.network.services.strapi.JsonWithIgnoredUnknownKeys
import com.metacto.strapikmm.errorhandling.errortype.UnAuthorizedException
import com.metacto.strapikmm.errorhandling.errortype.UnexpectedException
import io.ktor.client.call.body
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement

class NetworkErrorMapper {

    fun mapThrowable(throwable: Throwable): AppException {
        return UnexpectedException(
            code = UNEXPECTED,
            errorMessage = createErrorJsonResponse(throwable.message ?: throwable.toString(), -1),
            throwable = throwable
        )
    }

    fun mapToAppException(
        throwable: Throwable,
        errorMessage: String? = null,
        httpErrorCode: Int?,
    ): AppException {
        return UnexpectedException(
            code = httpErrorCode ?: -1,
            errorMessage = createErrorJsonResponse(
                errorMessage ?: throwable.message ?: throwable.toString(), httpErrorCode ?: -1
            ),
            throwable = throwable
        )
    }

    fun mapServerError(
        httpErrorCode: Int?,
        errorCode: Int?,
        errorMessage: String? = null,
        errorBody: String? = null,
        throwable: Throwable
    ): AppException {
        return when (httpErrorCode) {
            UNAUTHORIZED -> UnAuthorizedException(
                code = httpErrorCode,
                errorMessage = createErrorJsonResponse(
                    errorMessage ?: "The application has encountered an unknown error",
                    httpErrorCode
                ),
                throwable = throwable
            )

            else -> AppException(
                errorCode = errorCode ?: httpErrorCode ?: -1,
                errorMessage = createErrorJsonResponse(
                    errorMessage ?: "The application has encountered an unknown error",
                    errorCode ?: httpErrorCode ?: -1
                ),
                errorBody = errorBody,
                throwable = throwable
            )
        }
    }

    companion object {
        private const val UNEXPECTED = -102
        private const val UNAUTHORIZED = 401
        const val NO_INTERNET_CONNECTION = 4232
        const val SOMETHING_WRONG = 4222
        const val NO_INTERNET_CONNECTION_MESSAGE =
            "Hmm, there seems to be a problem with your internet connection."
        const val SOMETHING_WRONG_MESSAGE = "Oops, something went wrong. Please try again later."
    }
}

fun createErrorJsonResponse(message: String, code: Int): String {
    val errorMessage = if (isValidJson(message)) {
        val errorData =
            JsonFlatter.flat<NetworkError>(JsonWithIgnoredUnknownKeys.decodeFromString(message))
        val errorResponse =
            JsonWithIgnoredUnknownKeys.decodeFromJsonElement<NetworkError>(errorData)
        errorResponse.message ?: message
    } else {
        message
    }

    return JsonObject(
        mapOf(
            "message" to JsonPrimitive(errorMessage),
            "code" to JsonPrimitive(code)
        )
    ).toString()
}

fun isValidJson(jsonString: String): Boolean {
    return try {
        JsonWithIgnoredUnknownKeys.parseToJsonElement(jsonString)
        true
    } catch (e: Exception) {
        false
    }
}


