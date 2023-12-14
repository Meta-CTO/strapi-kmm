package com.swensonhe.strapikmm.util

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone

object DatetimeUtil {
    fun now(): LocalDateTime {
        val currentMoment: Instant = Clock.System.now()
        return currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    fun convertMillisecondsToLocalDateTime(milliseconds: Long): LocalDateTime {
        return milliseconds.toLocalDate()
    }

    fun convertMillisecondsToLocalDate(milliseconds: Long): LocalDate {
        return milliseconds.toLocalDate().toLocalDate()
    }

    fun isToday(localDate: LocalDate): Boolean {
        val now = now()
        return localDate.year == now.year &&
            localDate.monthNumber == now.monthNumber &&
            localDate.dayOfMonth == now.dayOfMonth
    }
}

fun LocalDateTime.now(): LocalDateTime {
    val currentMoment: Instant = Clock.System.now()
    return currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDateTime.toLocalDate(): LocalDate {
    return LocalDate(this.year, this.monthNumber, this.dayOfMonth)
}

fun Long.toLocalDate(): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())
}

fun Long.toInstant(): Instant {
    return Instant.fromEpochMilliseconds(this)
}

fun LocalDateTime.toEpochMilliseconds(): Long {
    return toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

fun LocalDateTime?.isBefore1DayFromNow(): Boolean {
    return if (this != null) {
        val durationDays = toInstant(TimeZone.currentSystemDefault())
            .until(Clock.System.now(), DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        durationDays >= 1
    } else
        true
}

fun LocalDateTime?.minutesFromNow(): Long {
    return if (this != null) {
        return toInstant(TimeZone.currentSystemDefault())
            .until(Clock.System.now(), DateTimeUnit.MINUTE, TimeZone.currentSystemDefault())
    } else {
        0
    }
}

fun LocalDate?.isBefore1DayFromNow(): Boolean {
    return if (this != null) {
        val localTimeDate = LocalDateTime(year, monthNumber, dayOfMonth, 0, 0, 0)
        val durationDays = localTimeDate.toInstant(TimeZone.currentSystemDefault())
            .until(Clock.System.now(), DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        durationDays >= 1
    } else
        true
}

fun LocalDate.plusDays(days: Int) = this.plus(days, DateTimeUnit.DAY)
fun LocalDate.plusDay() = this.plusDays(1)
fun LocalDate.plusYear() = this.plus(1, DateTimeUnit.YEAR)

fun LocalDate.minusDays(days: Int) = this.minus(days, DateTimeUnit.DAY)
fun LocalDate.minusDay() = this.minusDays(1)
fun LocalDate.minusYear() = this.minusYears(1)
fun LocalDate.minusYears(year: Int) = this.minus(year, DateTimeUnit.YEAR)
