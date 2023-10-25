package com.swensonhe.strapikmm.errorhandling

import com.swensonhe.strapikmm.errorhandling.errortype.UnAuthorizedException
import com.swensonhe.strapikmm.errorhandling.errortype.UnexpectedException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * A utility class for mapping network-related errors to application-specific exceptions.
 */
class NetworkErrorMapper {

    /**
     * Maps a [Throwable] to an [AppException] representing an unexpected error.
     *
     * @param throwable The [Throwable] to be mapped to an exception.
     * @return An [UnexpectedException] containing information about the unexpected error.
     */
    fun mapThrowable(throwable: Throwable): AppException {
        return UnexpectedException(
            code = UNEXPECTED,
            errorMessage = createErrorJsonResponse("$throwable", -1),
            throwable = throwable
        )
    }

    /**
     * Maps a server error response to an appropriate [AppException].
     *
     * @param httpErrorCode The HTTP status code associated with the error.
     * @param errorCode An optional error code providing more details about the error.
     * @param errorMessage A human-readable error message.
     * @param errorBody The body of the error response.
     * @param throwable The [Throwable] associated with the error.
     * @return An [AppException] representing the mapped server error.
     */
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

/**
 * Creates a JSON error response as a [String].
 *
 * @param message The error message to be included in the response.
 * @param code The error code to be included in the response.
 * @return A JSON error response as a [String].
 */
private fun createErrorJsonResponse(message: String, code: Int) = JsonObject(
    mapOf(
        "message" to JsonPrimitive(message),
        "code" to JsonPrimitive(code)
    )
).toString()