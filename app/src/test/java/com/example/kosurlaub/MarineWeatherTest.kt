package com.example.kosurlaub

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MarineWeatherTest {
    @Test
    fun `parser maps current marine values`() {
        val result = MarineWeatherParser.parse(
            """{"current":{"sea_surface_temperature":26.4,"wave_height":0.7,"wave_period":4.5,"wave_direction":123}}""",
            fetchedAtMillis = 42L
        )

        assertEquals(26.4, result.seaSurfaceTemperatureCelsius ?: 0.0, 0.001)
        assertEquals(0.7, result.waveHeightMeters ?: 0.0, 0.001)
        assertEquals(4.5, result.wavePeriodSeconds ?: 0.0, 0.001)
        assertEquals(123.0, result.waveDirectionDegrees ?: 0.0, 0.001)
        assertEquals(42L, result.fetchedAtMillis)
    }

    @Test
    fun `parser tolerates missing optional marine values`() {
        val result = MarineWeatherParser.parse("""{"current":{}}""", 7L)

        assertNull(result.seaSurfaceTemperatureCelsius)
        assertNull(result.waveHeightMeters)
        assertNull(result.wavePeriodSeconds)
        assertNull(result.waveDirectionDegrees)
    }
}
