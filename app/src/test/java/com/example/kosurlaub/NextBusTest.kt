package com.example.kosurlaub

import com.example.kosurlaub.ui.travel.BusDeparture
import com.example.kosurlaub.ui.travel.BusRoute
import com.example.kosurlaub.ui.travel.nextBus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class NextBusTest {

    private fun route(validity: String, vararg times: String) = BusRoute(
        from = "Tigaki", to = "Kos-Stadt", validity = validity,
        departures = times.map { BusDeparture(it) }, source = "test", asOf = "01.01.2026"
    )

    private val routes = listOf(
        route("Mo–Sa · Referenz", "08:10", "15:30", "22:15"),
        route("Sonntag · Referenz", "10:15", "18:00")
    )

    private fun at(year: Int, month: Int, day: Int, hour: Int, minute: Int) =
        Calendar.getInstance(TimeZone.getTimeZone("Europe/Athens")).apply {
            clear(); set(year, month - 1, day, hour, minute)
        }

    @Test
    fun picksNextDepartureOnWeekday() {
        // 16.09.2027 ist ein Donnerstag
        val bus = nextBus("Tigaki", "Kos-Stadt", at(2027, 9, 16, 15, 0), routes)!!
        assertEquals("15:30", bus.time)
        assertEquals(30, bus.minutesUntil)
    }

    @Test
    fun usesSundayTimetableOnSunday() {
        // 19.09.2027 ist ein Sonntag
        val bus = nextBus("Tigaki", "Kos-Stadt", at(2027, 9, 19, 9, 0), routes)!!
        assertEquals("10:15", bus.time)
    }

    @Test
    fun returnsNullAfterLastDeparture() {
        assertNull(nextBus("Tigaki", "Kos-Stadt", at(2027, 9, 16, 23, 0), routes))
    }

    @Test
    fun returnsNullForUnknownRoute() {
        assertNull(nextBus("Tigaki", "Mastichari", at(2027, 9, 16, 9, 0), routes))
    }
}
