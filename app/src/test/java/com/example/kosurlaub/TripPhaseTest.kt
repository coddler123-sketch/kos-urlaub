package com.example.kosurlaub

import com.example.kosurlaub.ui.home.TripDays
import com.example.kosurlaub.ui.home.TripPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class TripPhaseTest {
    private fun athens(y: Int, m: Int, d: Int, h: Int, min: Int) =
        Calendar.getInstance(TimeZone.getTimeZone("Europe/Athens")).apply {
            clear(); set(y, m - 1, d, h, min, 0)
        }.timeInMillis

    @Test
    fun phaseChangesAtArrivalAndReturnDeparture() {
        assertEquals(TripPhase.BEFORE, TripPhase.of(athens(2027, 9, 13, 17, 44)))
        assertEquals(TripPhase.ON_KOS, TripPhase.of(athens(2027, 9, 13, 17, 45)))
        assertEquals(TripPhase.ON_KOS, TripPhase.of(athens(2027, 9, 28, 11, 34)))
        assertEquals(TripPhase.AFTER, TripPhase.of(athens(2027, 9, 28, 11, 35)))
    }

    @Test
    fun tripDaysCoverFlightDaysInclusive() {
        val days = TripDays.all
        assertEquals(16, days.size)
        assertEquals("Mo", days.first().weekday)
        assertEquals("Di", days.last().weekday)
        assertEquals(4, TripDays.today(athens(2027, 9, 16, 9, 0))?.index)
        assertNull(TripDays.today(athens(2027, 9, 29, 9, 0)))
    }
}
