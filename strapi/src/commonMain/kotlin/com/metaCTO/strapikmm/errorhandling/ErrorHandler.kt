package com.metaCTO.strapikmm.errorhandling


fun handleError(throwable: Throwable): AppException {
    return if (throwable is AppException) {
        throwable
    } else {
        NetworkErrorMapper().mapThrowable(throwable)
    }
}
