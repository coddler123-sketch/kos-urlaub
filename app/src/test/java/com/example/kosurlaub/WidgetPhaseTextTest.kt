package com.example.kosurlaub

import com.example.kosurlaub.ui.home.AppClock
import com.example.kosurlaub.widget.WidgetPhaseText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WidgetPhaseTextTest {
    private fun kos(day: Int, hour: Int) = AppClock.millisOf("Europe/Athens", 2027, 9, day, hour, 0)

    @Test fun departureDayShowsTime() = assertEquals("Abflug 13:25 Uhr", WidgetPhaseText.departureDay())

    @Test fun vacationDayCounts() {
        assertEquals("Tag 4 von 16", WidgetPhaseText.vacationDay(kos(16, 12)))
    }

    @Test fun returnDayShowsFlightTime() {
        assertEquals("Rückflug 11:35 Uhr", WidgetPhaseText.vacationDay(kos(28, 8)))
    }

    @Test fun outsideTripIsNull() = assertNull(WidgetPhaseText.vacationDay(kos(1, 12)))
}
