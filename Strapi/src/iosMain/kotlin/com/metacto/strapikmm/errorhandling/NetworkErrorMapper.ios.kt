package com.metacto.strapikmm.errorhandling

import platform.Foundation.NSError
import platform.Foundation.NSLocalizedDescriptionKey

actual object NetworkErrorMapper {
    private const val ERROR_DOMAIN = "com.metacto.strapikmm"

    actual fun mapThrowable(throwable: Throwable): AppException {
        val errorMessage = throwable.message ?: throwable.toString()
        val error = NSError(
            code = NetworkMapperConstants.UNEXPECTED.toLong(),
            domain = ERROR_DOMAIN,
            userInfo = mapOf(NSLocalizedDescriptionKey to errorMessage)
        )


        return AppException(
            errorCode = NetworkMapperConstants.UNEXPECTED,
            errorMessage = error.localizedDescription,
            error = error
        )
    }

    fun mapThrowable(error: NSError): AppException {
        return AppException(
            errorCode = error.code.toInt(),
            errorMessage = error.localizedDescription,
            error = error
        )
    }

    actual fun mapToAppException(
        errorMessage: String,
        errorCode: Int
    ): AppException {
        val error = NSError(
            code = errorCode.toLong(),
            domain = ERROR_DOMAIN,
            userInfo = mapOf(NSLocalizedDescriptionKey to errorMessage)
        )

        return AppException(
            errorCode = errorCode,
            errorMessage = errorMessage,
            error = error
        )
    }
    actual fun mapToAppException(
        throwable: Throwable,
        errorMessage: String?,
        httpErrorCode: Int?,
    ): AppException {
        val errorCode = httpErrorCode ?: -1
        val message = errorMessage ?: throwable.message ?: throwable.toString()

        val error = NSError(
            code = errorCode.toLong(),
            domain = ERROR_DOMAIN,
            userInfo = mapOf(NSLocalizedDescriptionKey to message)
        )

        return AppException(
            errorCode = errorCode,
            errorMessage = error.localizedDescription,
            error = error
        )
    }

    actual fun mapServerError(
        httpErrorCode: Int?,
        errorCode: Int?,
        errorMessage: String?,
        errorBody: String?,
        throwable: Throwable
    ): AppException {
        val code =
            if (httpErrorCode == NetworkMapperConstants.UNAUTHORIZED) httpErrorCode else errorCode
        val message = errorMessage ?: "The application has encountered an unknown error"

        val error = NSError(
            code = code?.toLong() ?: -1,
            domain = ERROR_DOMAIN,
            userInfo = mapOf(NSLocalizedDescriptionKey to message)
        )

        return AppException(
            errorCode = code ?: -1,
            errorMessage = error.localizedDescription,
            error = error,
            errorBody = errorBody
        )
    }
}