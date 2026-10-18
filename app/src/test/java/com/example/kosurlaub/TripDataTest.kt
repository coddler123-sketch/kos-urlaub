package com.example.kosurlaub

import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TripDataTest {
    private fun millis(y: Int, m: Int, d: Int, h: Int, min: Int, zone: String) =
        ZonedDateTime.of(y, m, d, h, min, 0, 0, ZoneId.of(zone)).toInstant().toEpochMilli()

    @Test
    fun `outbound flight uses local airport times`() {
        assertEquals(millis(2027, 9, 13, 13, 25, "Europe/Berlin"), TripData.outbound.departureMillis())
        assertEquals(millis(2027, 9, 13, 17, 45, "Europe/Athens"), TripData.outbound.arrivalMillis())
    }

    @Test
    fun `inbound flight uses local airport times`() {
        assertEquals(millis(2027, 9, 28, 11, 35, "Europe/Athens"), TripData.inbound.departureMillis())
        assertEquals(millis(2027, 9, 28, 14, 0, "Europe/Berlin"), TripData.inbound.arrivalMillis())
    }

    @Test
    fun `flight durations are plausible`() {
        val outbound = TripData.outbound.arrivalMillis() - TripData.outbound.departureMillis()
        val inbound = TripData.inbound.arrivalMillis() - TripData.inbound.departureMillis()
        assertEquals(3 * 3_600_000L + 20 * 60_000L, outbound)
        assertEquals(3 * 3_600_000L + 25 * 60_000L, inbound)
    }

    @Test
    fun `summary shows local times and flight number`() {
        assertEquals(
            "Mo 13.09.2027 · Hamburg 13:25 → Kos 17:45 (Ortszeit) · Condor DE1640",
            TripData.outbound.summary()
        )
        assertEquals(
            "Di 28.09.2027 · Kos 11:35 → Hamburg 14:00 (Ortszeit) · Condor DE1641",
            TripData.inbound.summary()
        )
    }

    @Test
    fun `hotel coordinates are on Kos`() {
        assertTrue(TripData.HOTEL_LAT in 36.7..36.95)
        assertTrue(TripData.HOTEL_LON in 26.9..27.4)
    }
}
