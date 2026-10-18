package com.example.kosurlaub.ui.home

import java.util.Calendar

/**
 * Spruch des Tages: In den letzten Tagen vor dem Abflug passen die Countdown-Sprüche
 * (einer pro Tag), davor wechselt täglich ein Spruch aus dem allgemeinen Pool.
 */
internal fun phraseOfTheDay(
    countdown: Array<String>,
    daily: Array<String>,
    daysUntil: Int,
    epochDay: Long
): String = when {
    daysUntil < 0 -> countdown.last()
    daysUntil < countdown.size -> countdown[countdown.size - 1 - daysUntil]
    daily.isEmpty() -> countdown.first()
    else -> daily[Math.floorMod(epochDay, daily.size.toLong()).toInt()]
}

/** Tageszähler in Ortszeit des Geräts (wechselt um Mitternacht). */
internal fun localEpochDay(now: Calendar = Calendar.getInstance()): Long =
    (now.timeInMillis + now.get(Calendar.ZONE_OFFSET) + now.get(Calendar.DST_OFFSET)) / 86_400_000L
