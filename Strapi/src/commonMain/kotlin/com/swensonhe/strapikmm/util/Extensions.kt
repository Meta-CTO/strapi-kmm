package com.swensonhe.strapikmm.util

fun Boolean.toInt() = if (this) 1 else 0

fun Int.toBoolean() = this == 1