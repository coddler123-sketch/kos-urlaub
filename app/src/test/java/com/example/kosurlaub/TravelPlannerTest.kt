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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TravelPlannerTest {
    @Test
    fun `strong wind or high waves count as marine caution`() {
        assertTrue(isMarineCaution(weather(wind = 30), marine()))
        assertTrue(isMarineCaution(weather(), marine(waves = 1.3)))
    }

    @Test
    fun `calm weather is no marine caution`() {
        assertFalse(isMarineCaution(weather(), marine()))
    }

    private fun weather(
        wind: Int = 10,
        precipitation: Double = 0.0,
        uv: Double = 3.0
    ) = KosWeather(
        temperatureCelsius = 28,
        apparentTemperatureCelsius = 29,
        relativeHumidityPercent = 55,
        precipitationMm = precipitation,
        windSpeedKmh = wind,
        weatherCode = 0,
        isDay = true,
        uvIndex = uv
    )

    private fun marine(waves: Double = 0.4) = MarineWeather(
        seaSurfaceTemperatureCelsius = 26.0,
        waveHeightMeters = waves,
        wavePeriodSeconds = 4.0,
        waveDirectionDegrees = 100.0,
        fetchedAtMillis = 1L
    )
}
