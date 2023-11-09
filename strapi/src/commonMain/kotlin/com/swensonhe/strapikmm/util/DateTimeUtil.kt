package com.swensonhe.strapikmm.util

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone

/**
 * A utility object for handling date and time operations.
 */
object DatetimeUtil {
    /**
     * Get the current date and time in the system's default time zone.
     *
     * @return The current date and time as a [LocalDateTime].
     */
    fun now(): LocalDateTime {
        val currentMoment: Instant = Clock.System.now()
        return currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    /**
     * Convert a given number of milliseconds to a [LocalDateTime].
     *
     * @param milliseconds The number of milliseconds to convert.
     * @return The corresponding [LocalDateTime] for the given milliseconds.
     */
    fun convertMillisecondsToLocalDateTime(milliseconds: Long): LocalDateTime {
        return milliseconds.toLocalDate()
    }

    /**
     * Convert a given number of milliseconds to a [LocalDate].
     *
     * @param milliseconds The number of milliseconds to convert.
     * @return The corresponding [LocalDate] for the given milliseconds.
     */
    fun convertMillisecondsToLocalDate(milliseconds: Long): LocalDate {
        return milliseconds.toLocalDate().toLocalDate()
    }

    /**
     * Check if a given [LocalDate] is the same as the current date.
     *
     * @param localDate The [LocalDate] to compare.
     * @return `true` if the given date is the same as the current date; otherwise, `false`.
     */
    fun isToday(localDate: LocalDate): Boolean {
        val now = now()
        return localDate.year == now.year &&
                localDate.monthNumber == now.monthNumber &&
                localDate.dayOfMonth == now.dayOfMonth
    }
}

/**
 * Extension function to get the current date and time in the system's default time zone.
 *
 * @return The current date and time as a [LocalDateTime].
 */
fun LocalDateTime.now(): LocalDateTime {
    val currentMoment: Instant = Clock.System.now()
    return currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
}

/**
 * Extension function to convert a [LocalDateTime] to a [LocalDate].
 *
 * @return The [LocalDate] corresponding to the given [LocalDateTime].
 */
fun LocalDateTime.toLocalDate(): LocalDate {
    return LocalDate(this.year, this.monthNumber, this.dayOfMonth)
}

/**
 * Extension function to convert a given number of milliseconds to a [LocalDateTime].
 *
 * @param milliseconds The number of milliseconds to convert.
 * @return The corresponding [LocalDateTime] for the given milliseconds.
 */
fun Long.toLocalDate(): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())
}

/**
 * Extension function to convert a given number of milliseconds to an [Instant].
 *
 * @return The corresponding [Instant] for the given milliseconds.
 */
fun Long.toInstant(): Instant {
    return Instant.fromEpochMilliseconds(this)
}

/**
 * Extension function to convert a [LocalDateTime] to epoch milliseconds.
 *
 * @return The epoch milliseconds value of the given [LocalDateTime].
 */
fun LocalDateTime.toEpochMilliseconds(): Long {
    return toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

/**
 * Extension function to check if a [LocalDateTime] is before 1 day from the current time.
 *
 * @return `true` if the [LocalDateTime] is more than 1 day before the current time, otherwise `false`.
 */
fun LocalDateTime?.isBefore1DayFromNow(): Boolean {
    return if (this != null) {
        val durationDays = toInstant(TimeZone.currentSystemDefault())
            .until(Clock.System.now(), DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        durationDays >= 1
    } else
        true
}

/**
 * Extension function to check if a [LocalDate] is before 1 day from the current time.
 *
 * @return `true` if the [LocalDate] is more than 1 day before the current time, otherwise `false`.
 */
fun LocalDate?.isBefore1DayFromNow(): Boolean {
    return if (this != null) {
        val localTimeDate = LocalDateTime(year, monthNumber, dayOfMonth, 0, 0, 0)
        val durationDays = localTimeDate.toInstant(TimeZone.currentSystemDefault())
            .until(Clock.System.now(), DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        durationDays >= 1
    } else
        true
}

/**
 * Extension function to add a specified number of days to a [LocalDate].
 *
 * @param days The number of days to add.
 * @return The [LocalDate] after adding the specified number of days.
 */
fun LocalDate.plusDays(days: Int) = this.plus(days, DateTimeUnit.DAY)

/**
 * Extension function to add one day to a [LocalDate].
 *
 * @return The [LocalDate] after adding one day.
 */
fun LocalDate.plusDay() = this.plusDays(1)

/**
 * Extension function to add one year to a [LocalDate].
 *
 * @return The [LocalDate] after adding one year.
 */
fun LocalDate.plusYear() = this.plus(1, DateTimeUnit.YEAR)

/**
 * Extension function to subtract a specified number of days from a [LocalDate].
 *
 * @param days The number of days to subtract.
 * @return The [LocalDate] after subtracting the specified number of days.
 */
fun LocalDate.minusDays(days: Int) = this.minus(days, DateTimeUnit.DAY)

/**
 * Extension function to subtract one day from a [LocalDate].
 *
 * @return The [LocalDate] after subtracting one day.
 */
fun LocalDate.minusDay() = this.minusDays(1)

/**
 * Extension function to subtract one year from a [LocalDate].
 *
 * @return The [LocalDate] after subtracting one year.
 */
fun LocalDate.minusYear() = this.minusYears(1)

/**
 * Extension function to subtract a specified number of years from a [LocalDate].
 *
 * @param year The number of years to subtract.
 * @return The [LocalDate] after subtracting the specified number of years.
 */
fun LocalDate.minusYears(year: Int) = this.minus(year, DateTimeUnit.YEAR)
