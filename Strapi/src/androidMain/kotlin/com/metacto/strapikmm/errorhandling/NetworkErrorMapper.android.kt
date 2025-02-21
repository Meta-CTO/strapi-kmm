package com.metacto.strapikmm.errorhandling

actual object ErrorMapper {
    actual fun mapThrowable(throwable: Throwable): AppException {
        if (throwable is AppException) {
            return throwable
        }

        return AppException(
            errorCode = NetworkMapperConstants.UNEXPECTED,
            errorMessage = throwable.message ?: throwable.toString(),
            throwable = throwable
        )
    }

    actual fun mapToAppException(
        throwable: Throwable,
        errorMessage: String?,
        httpErrorCode: Int?,
    ): AppException {
        return AppException(
            errorCode = httpErrorCode ?: -1,
            errorMessage = throwable.message ?: throwable.toString(),
            throwable = throwable
        )
    }

    actual fun mapServerError(
        httpErrorCode: Int?,
        errorCode: Int?,
        errorMessage: String?,
        errorBody: String?,
        throwable: Throwable,
        headers: Map<String, List<String>>?
    ): AppException {
        val code =
            if (httpErrorCode == NetworkMapperConstants.UNAUTHORIZED) httpErrorCode else errorCode
        return AppException(
            errorBody = errorBody,
            errorCode = code ?: -1,
            errorMessage = errorMessage ?: "The application has encountered an unknown error",
            throwable = throwable,
            headers = headers
        )
    }

    actual fun mapToAppException(
        errorMessage: String,
        errorCode: Int
    ): AppException {
        return AppException(
            errorCode = errorCode,
            errorMessage = errorMessage
        )
    }

    actual fun getNetworkConnectionException(): AppException {
        return AppException(
            errorCode = NetworkMapperConstants.NO_INTERNET_CONNECTION,
            errorMessage = NetworkMapperConstants.NO_INTERNET_CONNECTION_MESSAGE
        )
    }
}