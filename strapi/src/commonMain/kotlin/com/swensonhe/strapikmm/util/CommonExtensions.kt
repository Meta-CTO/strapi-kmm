package com.swensonhe.strapikmm.util

import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Checks if the receiver element matches any of the provided items.
 *
 * @param items The items to compare the receiver element with.
 * @return `true` if the receiver element matches any of the items; `false` otherwise.
 */
fun <T> T.matches(vararg items: T): Boolean {
    return items.contains(this)
}

/**
 * Converts a string to a list containing only the string itself.
 *
 * @return A list containing the original string.
 */
fun String.list() = listOf(this)

/**
 * Returns the receiver integer or 0 if it's null.
 *
 * @return The receiver integer or 0 if it's null.
 */
fun Int?.orZero() = this ?: 0

/**
 * Returns the receiver float or 0.0 if it's null.
 *
 * @return The receiver float or 0.0 if it's null.
 */
fun Float?.orZero() = this ?: 0f

/**
 * Returns the receiver float or 1.0 if it's null.
 *
 * @return The receiver float or 1.0 if it's null.
 */
fun Float?.orOne() = this ?: 1f

/**
 * Executes the provided block if the receiver boolean is `true`.
 *
 * @param block The block to execute if the receiver boolean is `true`.
 * @return The result of the block's execution or `null` if the receiver boolean is `false`.
 */
fun <R> Boolean?.ifTrue(block: () -> R): R? {
    return if (this == true) block() else null
}

/**
 * Executes the provided suspend block if the receiver boolean is `true`.
 *
 * @param block The suspend block to execute if the receiver boolean is `true`.
 * @return The result of the block's execution or `null` if the receiver boolean is `false`.
 */
suspend fun <R> Boolean?.suspendIfTrue(block: suspend () -> R): R? {
    return if (this == true) block() else null
}

/**
 * Returns the receiver double or 0.0 if it's null.
 *
 * @return The receiver double or 0.0 if it's null.
 */
fun Double?.orZero() = this ?: 0.0

/**
 * Returns the receiver double or 1.0 if it's null.
 *
 * @return The receiver double or 1.0 if it's null.
 */
fun Double?.orOne() = this ?: 1.0

/**
 * Returns the receiver string if it's not empty; otherwise, returns `null`.
 *
 * @return The receiver string or `null` if it's empty.
 */
fun String.takeIfNotEmpty() = this.takeIf { it.isNotEmpty() }

/**
 * Converts a boolean to an integer (1 for `true` and 0 for `false`).
 *
 * @return 1 if the receiver is `true`, 0 if the receiver is `false`.
 */
fun Boolean.toInt() = if (this) 1 else 0

/**
 * Converts an integer to a boolean (true for 1 and false for other values).
 *
 * @return `true` if the receiver is 1, `false` for other values.
 */
fun Int.toBoolean() = this == 1

/**
 * Returns `null` if the receiver string is empty; otherwise, returns the string.
 *
 * @return `null` if the receiver string is empty; otherwise, the string itself.
 */
fun String.nullIfEmpty() = this.ifEmpty { null }

/**
 * Converts an integer to a two-digit format string by adding a leading '0' if necessary.
 *
 * @return The receiver integer formatted as a two-digit string.
 */
fun Int.to2DigitsFormat(): String {
    return if (this < 10) "0$this" else this.toString()
}

/**
 * Resumes the continuation with the provided value if it is still active.
 *
 * @param value The value to resume the continuation with.
 */
fun <T> CancellableContinuation<T>.resumeIfActive(value: T) {
    if (isActive) resume(value)
}

/**
 * Resumes the continuation with the provided exception if it is still active.
 *
 * @param throwable The exception to resume the continuation with.
 */
fun CancellableContinuation<*>.exceptionIfActive(throwable: Throwable) {
    if (isActive) resumeWithException(throwable)
}

/**
 * Conditionally applies the provided block to the receiver object based on a given condition.
 *
 * @param condition The condition to determine if the block should be applied.
 * @param block The block to apply to the receiver object.
 * @return The receiver object with the block applied if the condition is `true`; otherwise, the original receiver object.
 */
fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    return apply {
        if (condition) block()
    }
}
