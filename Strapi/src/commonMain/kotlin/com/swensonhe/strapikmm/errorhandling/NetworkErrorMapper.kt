package com.swensonhe.strapikmm.errorhandling

import com.swensonhe.strapikmm.errorhandling.errortype.UnAuthorizedException
import com.swensonhe.strapikmm.errorhandling.errortype.UnexpectedException
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
                    errorCode ?: -1
                ),
                throwable = throwable
            )

            else -> AppException(
                errorCode = httpErrorCode ?: UNEXPECTED,
                errorMessage = createErrorJsonResponse(
                    errorMessage ?: "The application has encountered an unknown error",
                    errorCode ?: -1
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