package com.example.kosurlaub.ui.home

import com.example.kosurlaub.TripData
import java.util.Calendar
import java.util.TimeZone

/** Wo die Reise gerade steht, abgeleitet aus Abflug (Hamburg) und Rückflug (Kos). */
enum class TripPhase {
    BEFORE,
    ON_KOS,
    AFTER;

    companion object {
        fun of(nowMillis: Long): TripPhase = when {
            nowMillis < TripData.outbound.arrivalMillis() -> BEFORE
            nowMillis < TripData.inbound.departureMillis() -> ON_KOS
            else -> AFTER
        }
    }
}

/** Tage vor dem Rückflug, ab denen die Rückflug-Bordkarte auf Heute erscheint. */
const val RETURN_PASS_LEAD_DAYS = 5

/** Die Rückflug-Bordkarte zeigt sich ab [RETURN_PASS_LEAD_DAYS] Tagen vor dem Rückflug, bis zum Abflug. */
fun isReturnPassVisible(nowMillis: Long): Boolean {
    val departure = TripData.inbound.departureMillis()
    return nowMillis in (departure - RETURN_PASS_LEAD_DAYS * 24L * 60 * 60 * 1000) until departure
}

/** Ein Reisetag für die Tagesleiste. [index] zählt ab 1 (13.09. = Tag 1). */
data class TripDay(val index: Int, val year: Int, val month: Int, val day: Int, val weekday: String)

object TripDays {
    private val weekdays = listOf("So", "Mo", "Di", "Mi", "Do", "Fr", "Sa")

    /** Alle Kalendertage vom Hinflug- bis zum Rückflugtag, Ortszeit Kos. */
    val all: List<TripDay> by lazy {
        val cal = Calendar.getInstance(TimeZone.getTimeZone(TripData.inbound.fromZone)).apply {
            clear()
            set(TripData.outbound.year, TripData.outbound.month - 1, TripData.outbound.day)
        }
        val last = Calendar.getInstance(TimeZone.getTimeZone(TripData.inbound.fromZone)).apply {
            clear()
            set(TripData.inbound.year, TripData.inbound.month - 1, TripData.inbound.day)
        }
        buildList {
            var i = 1
            while (!cal.after(last)) {
                add(
                    TripDay(
                        i++, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.DAY_OF_MONTH), weekdays[cal.get(Calendar.DAY_OF_WEEK) - 1]
                    )
                )
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    /** Der Reisetag, der in Kos gerade läuft, oder null außerhalb der Reise. */
    fun today(nowMillis: Long): TripDay? {
        val cal = Calendar.getInstance(TimeZone.getTimeZone(TripData.inbound.fromZone)).apply { timeInMillis = nowMillis }
        return all.firstOrNull {
            it.year == cal.get(Calendar.YEAR) && it.month == cal.get(Calendar.MONTH) + 1 &&
                it.day == cal.get(Calendar.DAY_OF_MONTH)
        }
    }

    /** "TAG 4/16" für den laufenden Reisetag, sonst null. */
    fun label(nowMillis: Long): String? = today(nowMillis)?.let { "TAG ${it.index}/${all.size}" }
}
