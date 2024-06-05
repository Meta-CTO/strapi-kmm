package com.metacto.strapikmm.errorhandling


fun handleError(throwable: Throwable): AppException {
    return if (throwable is AppException) {
        throwable
    } else {
        NetworkErrorMapper().mapThrowable(throwable)
    }
}

fun String.mapError(
    code: Int,
): Throwable {
    return Throwable("{\"message\": \"$this\",\"code\": $code}")
}