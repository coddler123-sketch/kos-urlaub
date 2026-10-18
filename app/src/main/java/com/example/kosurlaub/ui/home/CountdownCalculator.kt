package com.example.kosurlaub.ui.home
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
import java.util.Locale
import java.util.concurrent.TimeUnit

data class RemainingTime(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long = 0L,
    val isReached: Boolean = false,
    val isOnVacation: Boolean = false,
    val isOver: Boolean = false
)

object CountdownCalculator {

    // Startpunkt des Countdowns (genau 1 Jahr vor Abflug)
    private const val START_YEAR = 2026
    private const val START_MONTH = Calendar.SEPTEMBER
    private const val START_DAY = 13

    private fun calendarAt(millis: Long, zone: String) =
        Calendar.getInstance(TimeZone.getTimeZone(zone)).apply { timeInMillis = millis }

    /** Abflug in Hamburger Ortszeit. */
    fun departureCalendar(): Calendar =
        calendarAt(TripData.outbound.departureMillis(), TripData.outbound.fromZone)

    fun progressFraction(now: Calendar = AppClock.calendar()): Float {
        val start = Calendar.getInstance().apply {
            set(START_YEAR, START_MONTH, START_DAY, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val total = (TripData.outbound.departureMillis() - start.timeInMillis).toFloat()
        val elapsed = (now.timeInMillis - start.timeInMillis).toFloat()
        return (elapsed / total).coerceIn(0f, 1f)
    }

    fun calculate(now: Calendar = AppClock.calendar()): RemainingTime {
        val remainingMillis = (TripData.outbound.departureMillis() - now.timeInMillis).coerceAtLeast(0L)
        val days = TimeUnit.MILLISECONDS.toDays(remainingMillis)
        val afterDays = remainingMillis - TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(afterDays)
        val afterHours = afterDays - TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(afterHours)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(afterHours - TimeUnit.MINUTES.toMillis(minutes))
        val departureDayStart = midnight(departureCalendar())
        val isOver = now.timeInMillis >= TripData.inbound.departureMillis()
        return RemainingTime(
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            isReached = now.timeInMillis >= departureDayStart.timeInMillis,
            isOnVacation = now.timeInMillis >= TripData.outbound.arrivalMillis() && !isOver,
            isOver = isOver
        )
    }

    fun daysUntilDeparture(now: Calendar = AppClock.calendar()): Int {
        val today = midnight(now.clone() as Calendar)
        val departure = midnight(departureCalendar())
        val diffDays = (departure.timeInMillis - today.timeInMillis) / 86_400_000.0
        return Math.round(diffDays).toInt()
    }

    fun midnight(cal: Calendar): Calendar = cal.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    fun isFlightDay(now: Calendar = AppClock.calendar()): Boolean {
        val departure = departureCalendar()
        val local = Calendar.getInstance(departure.timeZone).apply { timeInMillis = now.timeInMillis }
        return local.get(Calendar.YEAR) == departure.get(Calendar.YEAR) &&
            local.get(Calendar.DAY_OF_YEAR) == departure.get(Calendar.DAY_OF_YEAR)
    }
}

/**
 * Hilfsfunktion, um ein ISO-Datum ("yyyy-MM-dd") in ein deutsches Wochentagskürzel ("Mo", "Di" etc.) umzuwandeln.
 */
fun String.toShortGermanWeekday(): String {
    return try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(this) ?: return "?"
        val cal = Calendar.getInstance().apply { time = date }
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Mo"
            Calendar.TUESDAY -> "Di"
            Calendar.WEDNESDAY -> "Mi"
            Calendar.THURSDAY -> "Do"
            Calendar.FRIDAY -> "Fr"
            Calendar.SATURDAY -> "Sa"
            else -> "So"
        }
    } catch (e: Exception) {
        "?"
    }
}
