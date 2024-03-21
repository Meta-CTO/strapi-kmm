package com.metacto.strapikmm.util

import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun <T> T.matches(vararg items: T): Boolean {
    return items.contains(this)
}

fun String.list() = listOf(this)

fun Int?.orZero() = this ?: 0

fun Float?.orZero() = this ?: 0f

fun Float?.orOne() = this ?: 1f

fun <R> Boolean?.ifTrue(block: () -> R): R? {
    return if (this == true) block() else null
}

suspend fun <R> Boolean?.suspendIfTrue(block: suspend () -> R): R? {
    return if (this == true) block() else null
}

fun Double?.orZero() = this ?: 0.0

fun Double?.orOne() = this ?: 1.0

fun String.takeIfNotEmpty() = this.takeIf { it.isNotEmpty() }

fun Boolean.toInt() = if (this) 1 else 0

fun Int.toBoolean() = this == 1

fun String.nullIfEmpty() = this.ifEmpty { null }

fun Int.to2DigitsFormat(): String {
    return if (this < 10) "0$this" else this.toString()
}

fun <T> CancellableContinuation<T>.resumeIfActive(value: T) {
    if (isActive) resume(value)
}

fun CancellableContinuation<*>.exceptionIfActive(throwable: Throwable) {
    if (isActive) resumeWithException(throwable)
}

fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    return apply {
        if (condition) block()
    }
}