package com.example.kosurlaub.ui.home

import android.content.Context
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/** Sonnenauf-/untergang als Minuten seit Mitternacht Ortszeit. Rein berechnet (NOAA-Näherung, ±2 Minuten). */
object SunTimes {
    data class Result(val sunriseMinute: Int, val sunsetMinute: Int) {
        val sunrise: String get() = format(sunriseMinute)
        val sunset: String get() = format(sunsetMinute)

        private fun format(minute: Int) = "%02d:%02d".format(minute / 60, minute % 60)
    }

    fun compute(lat: Double, lon: Double, year: Int, month: Int, day: Int, zoneId: String): Result? {
        val zone = TimeZone.getTimeZone(zoneId)
        val cal = Calendar.getInstance(zone).apply {
            clear()
            set(year, month - 1, day, 12, 0, 0)
        }
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val offsetMinutes = zone.getOffset(cal.timeInMillis) / 60_000.0
        val g = 2 * PI / 365 * (dayOfYear - 1)
        val eqTime = 229.18 * (0.000075 + 0.001868 * cos(g) - 0.032077 * sin(g) -
            0.014615 * cos(2 * g) - 0.040849 * sin(2 * g))
        val decl = 0.006918 - 0.399912 * cos(g) + 0.070257 * sin(g) - 0.006758 * cos(2 * g) +
            0.000907 * sin(2 * g) - 0.002697 * cos(3 * g) + 0.00148 * sin(3 * g)
        val latRad = Math.toRadians(lat)
        val cosHa = cos(Math.toRadians(90.833)) / (cos(latRad) * cos(decl)) - tan(latRad) * tan(decl)
        if (cosHa !in -1.0..1.0) return null // Polartag/-nacht, auf Kos nie
        val haDeg = Math.toDegrees(acos(cosHa))
        val rise = 720 - 4 * (lon + haDeg) - eqTime + offsetMinutes
        val set = 720 - 4 * (lon - haDeg) - eqTime + offsetMinutes
        return Result(rise.toInt(), set.toInt())
    }
}
