package com.example.kosurlaub

import com.example.kosurlaub.ui.home.TripDays
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class TripDayLabelTest {

    private fun millis(year: Int, month: Int, day: Int) =
        Calendar.getInstance(TimeZone.getTimeZone("Europe/Athens")).apply {
            clear(); set(year, month - 1, day, 12, 0)
        }.timeInMillis

    @Test
    fun labelsDayWithinTrip() {
        assertEquals("TAG 4/16", TripDays.label(millis(2027, 9, 16)))
    }

    @Test
    fun noLabelOutsideTrip() {
        assertNull(TripDays.label(millis(2027, 10, 5)))
    }
}
