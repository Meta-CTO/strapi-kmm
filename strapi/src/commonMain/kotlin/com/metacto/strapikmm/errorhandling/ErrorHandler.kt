package com.metacto.strapikmm.errorhandling


fun handleError(throwable: Throwable): AppException {
    return if (throwable is AppException) {
        throwable
    } else {
        ErrorMapper.mapThrowable(throwable)
    }
}