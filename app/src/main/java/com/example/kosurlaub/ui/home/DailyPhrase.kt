package com.example.kosurlaub.ui.home

import java.util.Calendar

/** Ab so vielen Tagen vor dem Abflug kommen die Sprüche aus dem Pool für die weite Ferne. */
internal const val FAR_PHRASE_DAYS = 200

/**
 * Spruch des Tages: In den letzten Tagen vor dem Abflug passen die Countdown-Sprüche
 * (einer pro Tag), davor wechselt täglich ein Spruch aus dem allgemeinen Pool, bei mehr als
 * [FAR_PHRASE_DAYS] Tagen aus dem Pool für die weite Ferne (falls vorhanden).
 */
internal fun phraseOfTheDay(
    countdown: Array<String>,
    daily: Array<String>,
    daysUntil: Int,
    epochDay: Long,
    far: Array<String> = emptyArray()
): String {
    val pool = if (daysUntil > FAR_PHRASE_DAYS && far.isNotEmpty()) far else daily
    return when {
        daysUntil < 0 -> countdown.last()
        daysUntil < countdown.size -> countdown[countdown.size - 1 - daysUntil]
        pool.isEmpty() -> countdown.first()
        else -> pool[Math.floorMod(epochDay, pool.size.toLong()).toInt()]
    }
}

/** Tageszähler in Ortszeit des Geräts (wechselt um Mitternacht). */
internal fun localEpochDay(now: Calendar = Calendar.getInstance()): Long =
    (now.timeInMillis + now.get(Calendar.ZONE_OFFSET) + now.get(Calendar.DST_OFFSET)) / 86_400_000L
