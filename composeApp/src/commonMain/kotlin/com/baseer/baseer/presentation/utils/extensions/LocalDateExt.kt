package com.baseer.baseer.presentation.utils.extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number


/*
 * Helper utilities for working with kotlinx.datetime.LocalDate.  The standard
 * LocalDate class does not provide `withYear`, `withMonth` or `withDayOfMonth`
 * methods like its java.time counterpart, so we implement a small set of
 * convenience functions below.  These helpers also take care to coerce the
 * day of month into the valid range for the given year and month.
 */

/** Returns `true` if the given year is a leap year in the Gregorian calendar. */
fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

/**
 * Returns the number of days in a given month for a specific year.  February has
 * 29 days in a leap year and 28 otherwise; April, June, September and November
 * have 30; the remaining months have 31.
 */
fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (isLeapYear(year)) 29 else 28
    else -> 30
}

/**
 * Creates a copy of this [LocalDate] with optionally updated year, month and day
 * values.  If a new day value is invalid for the resulting year and
 * month, it is coerced into the valid range (e.g., selecting 31 February
 * becomes 28 February or 29 in a leap year).
 */
fun LocalDate.copyWith(
    year: Int = this.year,
    month: Int = this.month.number,
    day: Int = this.day
): LocalDate {
    val coercedDay = day.coerceIn(1, daysInMonth(year, month))
    return LocalDate(year, month, coercedDay)
}

/**
 * Extension functions mirroring the `java.time.LocalDate` API.  These provide
 * convenience methods for comparing dates and make the surrounding code more
 * readable.  `kotlinx.datetime.LocalDate` implements `Comparable`, so we can
 * delegate to the standard comparison operators.
 */
fun LocalDate.isBefore(other: LocalDate): Boolean = this < other
fun LocalDate.isAfter(other: LocalDate): Boolean = this > other

fun LocalDate.coerceIn(min: LocalDate, max: LocalDate): LocalDate {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}