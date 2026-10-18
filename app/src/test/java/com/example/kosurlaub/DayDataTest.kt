package com.example.kosurlaub

import com.example.kosurlaub.ui.home.DayEntry
import com.example.kosurlaub.ui.home.DayEntryStore
import com.example.kosurlaub.ui.home.diaryStats
import com.example.kosurlaub.ui.home.SunTimes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DayDataTest {
    @Test
    fun sunTimesTigakiMidSeptemberAreInPlausibleWindow() {
        val r = SunTimes.compute(TripData.HOTEL_LAT, TripData.HOTEL_LON, 2027, 9, 16, "Europe/Athens")
        assertNotNull(r)
        // Erwartung grob 06:55 / 19:20 Ortszeit (EEST); Fenster statt Punktwert.
        assertTrue(r!!.sunrise, r.sunriseMinute in (6 * 60 + 40)..(7 * 60 + 10))
        assertTrue(r.sunset, r.sunsetMinute in (19 * 60 + 5)..(19 * 60 + 35))
    }

    @Test
    fun daysGetShorterBetweenArrivalAndDeparture() {
        val a = SunTimes.compute(TripData.HOTEL_LAT, TripData.HOTEL_LON, 2027, 9, 13, "Europe/Athens")!!
        val b = SunTimes.compute(TripData.HOTEL_LAT, TripData.HOTEL_LON, 2027, 9, 28, "Europe/Athens")!!
        assertTrue(b.sunsetMinute - b.sunriseMinute < a.sunsetMinute - a.sunriseMinute)
    }

    @Test
    fun entryRoundTrip() {
        val entries = listOf(
            DayEntry(DayEntry.Kind.NOTE, "Gyros | top"),
            DayEntry(DayEntry.Kind.PHOTO, "p1.jpg"),
            DayEntry(DayEntry.Kind.PLACE, "trip-zia-sunset")
        )
        assertEquals(entries, DayEntryStore.decode(DayEntryStore.encode(entries)))
        assertEquals(emptyList<DayEntry>(), DayEntryStore.decode(null))
    }

    @Test
    fun diaryStatsCountsEntriesAndDays() {
        val stats = diaryStats(
            visitedCount = 2,
            entriesByDay = mapOf(
                1 to listOf(DayEntry(DayEntry.Kind.NOTE, "a"), DayEntry(DayEntry.Kind.PHOTO, "p.jpg")),
                3 to listOf(DayEntry(DayEntry.Kind.PLACE, "x")),
                5 to emptyList()
            )
        )
        assertEquals(2, stats.places)
        assertEquals(1, stats.notes)
        assertEquals(1, stats.photos)
        assertEquals(2, stats.daysWithEntries)
    }
}
