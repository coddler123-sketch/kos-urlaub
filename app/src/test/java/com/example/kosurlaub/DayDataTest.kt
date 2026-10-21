package com.example.kosurlaub

import com.example.kosurlaub.ui.home.SunTimes
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
}
