package com.example.kosurlaub.widget

import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.home.TripDays
import java.util.Locale

/** Kurztexte für das Widget je Reisephase: Abflugtag, Urlaubstag, Rückflugtag. Null = Standardtext. */
object WidgetPhaseText {
    private fun time(h: Int, m: Int) = String.format(Locale.GERMAN, "%02d:%02d", h, m)

    fun departureDay(): String = "Abflug ${time(TripData.outbound.departHour, TripData.outbound.departMinute)} Uhr"

    /** Während des Urlaubs "Tag 4 von 16", am Rückflugtag die Abflugzeit. */
    fun vacationDay(nowMillis: Long): String? {
        val day = TripDays.today(nowMillis) ?: return null
        if (day.index == TripDays.all.size) {
            return "Rückflug ${time(TripData.inbound.departHour, TripData.inbound.departMinute)} Uhr"
        }
        return "Tag ${day.index} von ${TripDays.all.size}"
    }
}
