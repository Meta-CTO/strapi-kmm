package com.swensonhe.strapikmm.util

fun <T> T.matches(vararg items: T): Boolean {
    return items.contains(this)
}

fun String?.nullIfEmpty() = if (isNullOrEmpty()) null else this

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