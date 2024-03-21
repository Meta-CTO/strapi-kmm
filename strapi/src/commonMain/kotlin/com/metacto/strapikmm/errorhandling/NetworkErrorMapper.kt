package com.metacto.strapikmm.errorhandling

import com.metacto.strapikmm.errorhandling.errortype.UnAuthorizedException
import com.metacto.strapikmm.errorhandling.errortype.UnexpectedException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class NetworkErrorMapper {

    fun mapThrowable(throwable: Throwable): AppException {
        return UnexpectedException(
            code = UNEXPECTED,
            errorMessage = createErrorJsonResponse("$throwable", -1),
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
    }
}

private fun createErrorJsonResponse(message: String, code: Int) = JsonObject(
    mapOf(
        "message" to JsonPrimitive(message),
        "code" to JsonPrimitive(code)
    )
).toString()