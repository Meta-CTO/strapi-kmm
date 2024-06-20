package com.metacto.strapikmm.errorhandling

object NetworkMapperConstants {
    const val UNEXPECTED = -102
    const val UNAUTHORIZED = 401
    const val NO_INTERNET_CONNECTION = 4232
    const val SOMETHING_WRONG = 4222
    const val NO_INTERNET_CONNECTION_MESSAGE =
        "Hmm, there seems to be a problem with your internet connection."
    const val SOMETHING_WRONG_MESSAGE = "Oops, something went wrong. Please try again later."
}

expect object ErrorMapper {

    fun mapThrowable(throwable: Throwable): AppException

    fun mapToAppException(
        errorMessage: String,
        errorCode: Int
    ): AppException

    fun mapToAppException(
        throwable: Throwable,
        errorMessage: String? = null,
        httpErrorCode: Int?,
    ): AppException

    fun mapServerError(
        httpErrorCode: Int?,
        errorCode: Int?,
        errorMessage: String? = null,
        errorBody: String? = null,
        throwable: Throwable
    ): AppException
}

inline fun <reified T> executeCatching(block: () -> T): T {
    try{
        return block()
    } catch (e: Throwable) {
        throw ErrorMapper.mapThrowable(e)
    }
}