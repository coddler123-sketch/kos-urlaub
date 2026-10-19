package com.example.kosurlaub

import com.example.kosurlaub.ui.compass.CompassTip
import com.example.kosurlaub.ui.compass.tipOfTheDay
import com.example.kosurlaub.ui.weather.KosWeather
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TipOfTheDayTest {

    private fun tip(id: String, category: String) =
        CompassTip(category = category, title = id, description = "", note = "", id = id)

    private val tips = listOf(
        tip("trip-a", "Ausflüge"), tip("trip-b", "Ausflüge"),
        tip("beach-a", "Strände"), tip("food-a", "Essen"),
        tip("hotel", "Unterkünfte"), tip("bus", "Mobilität")
    )

    private fun weather(wind: Int = 10, temp: Int = 25, rain: Double = 0.0) = KosWeather(
        temperatureCelsius = temp, apparentTemperatureCelsius = temp, relativeHumidityPercent = 50,
        precipitationMm = rain, windSpeedKmh = wind, weatherCode = 0, isDay = true
    )

    @Test
    fun windyDayPicksExcursion() {
        assertEquals("Ausflüge", tipOfTheDay(tips, weather(wind = 35), 0)!!.tip.category)
    }

    @Test
    fun hotDryDayPicksBeach() {
        assertEquals("Strände", tipOfTheDay(tips, weather(temp = 31), 0)!!.tip.category)
    }

    @Test
    fun rainyDayPicksFood() {
        assertEquals("Essen", tipOfTheDay(tips, weather(rain = 2.0), 0)!!.tip.category)
    }

    @Test
    fun sameDaySameTipDifferentDayRotates() {
        val w = weather(wind = 35)
        assertEquals(tipOfTheDay(tips, w, 5)!!.tip.id, tipOfTheDay(tips, w, 5)!!.tip.id)
        assertEquals(setOf("trip-a", "trip-b"), (0L..1L).map { tipOfTheDay(tips, w, it)!!.tip.id }.toSet())
    }

    @Test
    fun neverPicksHotelOrMobility() {
        (0L..20L).forEach { day ->
            val category = tipOfTheDay(tips, null, day)!!.tip.category
            assertEquals(true, category in setOf("Ausflüge", "Strände", "Essen"))
        }
    }

    @Test
    fun emptyListGivesNull() {
        assertNull(tipOfTheDay(emptyList(), null, 0))
    }
}
