package com.metacto.strapikmm.errorhandling

import com.rickclephas.kmp.nserrorkt.asNSError
import platform.Foundation.NSError
import platform.Foundation.NSLocalizedDescriptionKey
import kotlin.native.internal.ObjCErrorException

actual object ErrorMapper {
    private const val ERROR_DOMAIN = "com.metacto.strapikmm"

    actual fun mapThrowable(throwable: Throwable): AppException {
        if (throwable is ObjCErrorException) {
            return mapThrowable(throwable)
        }
        val errorMessage = throwable.message ?: throwable.toString()
        val error = createNsError(
            httpErrorCode = null,
            errorCode = NetworkMapperConstants.UNEXPECTED,
            errorMessage = errorMessage,
            errorBody = null
        )
        return AppException(error = error)
    }

    fun mapThrowable(error: ObjCErrorException): AppException {
        return AppException(error = error.asNSError())
    }

    fun mapThrowable(error: NSError): AppException {
        return AppException(error = error)
    }

    actual fun mapToAppException(
        errorMessage: String,
        errorCode: Int
    ): AppException {
        val error = createNsError(
            httpErrorCode = null,
            errorCode = errorCode,
            errorMessage = errorMessage,
            errorBody = null
        )

        return AppException(error = error)
    }
    actual fun mapToAppException(
        throwable: Throwable,
        errorMessage: String?,
        httpErrorCode: Int?,
    ): AppException {
        val errorCode = httpErrorCode ?: -1
        val message = errorMessage ?: throwable.message ?: throwable.toString()

        val error = createNsError(
            httpErrorCode = httpErrorCode,
            errorCode = errorCode,
            errorMessage = message,
            errorBody = null
        )

        return AppException(error = error)
    }

    actual fun mapServerError(
        httpErrorCode: Int?,
        errorCode: Int?,
        errorMessage: String?,
        errorBody: String?,
        throwable: Throwable
    ): AppException {
        val message = errorMessage ?: "The application has encountered an unknown error"

        val error = createNsError(
            httpErrorCode = httpErrorCode,
            errorCode = errorCode,
            errorMessage = message,
            errorBody = errorBody
        )
        return AppException(error = error)
    }

    private fun createNsError(
        httpErrorCode: Int?,
        errorCode: Int?,
        errorMessage: String?,
        errorBody: String?,
    ): NSError {
        val userInfo = mutableMapOf<Any?, Any>()
        errorMessage?.let { userInfo.put(NSLocalizedDescriptionKey.orEmpty(), it) }
        errorBody?.let { userInfo.put("errorBody", it) }
        httpErrorCode?.let { userInfo.put("httpErrorCode", it) }
        errorCode?.let { userInfo.put("errorCode", it) }
        return NSError(
            code = errorCode?.toLong() ?: -1,
            domain = ERROR_DOMAIN,
            userInfo = userInfo
        )
    }
}