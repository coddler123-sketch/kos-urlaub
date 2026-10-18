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
import org.junit.Test

class WeatherReportFormatterTest {
    @Test
    fun formatsSpokenWeatherReportWithThreeDayForecast() {
        val weather = KosWeather(
            temperatureCelsius = 26,
            apparentTemperatureCelsius = 22,
            relativeHumidityPercent = 53,
            precipitationMm = 0.0,
            windSpeedKmh = 35,
            weatherCode = 0,
            isDay = true,
            forecast = listOf(
                KosForecastDay("2026-06-15", 23, 31, 10, 0.0, 1),
                KosForecastDay("2026-06-16", 22, 30, 20, 0.2, 2),
                KosForecastDay("2026-06-17", 21, 29, 45, 1.4, 61)
            )
        )

        val report = WeatherReportFormatter.spokenReport(weather)

        assertEquals(
            "Wetterbericht fuer Tigaki auf Kos. Aktuell sind es 26 Grad, gefuehlt 22 Grad. " +
                "Die Luftfeuchtigkeit liegt bei 53 Prozent. Der Wind weht mit 35 Kilometern pro Stunde. " +
                "Es wird kein Regen gemeldet. Die Vorschau: Montag 23 bis 31 Grad, meist sonnig. " +
                "Dienstag 22 bis 30 Grad, leicht bewoelkt. Mittwoch 21 bis 29 Grad, Regen moeglich.",
            report
        )
    }

    @Test
    fun formatsCompactForecastLabels() {
        val forecast = listOf(
            KosForecastDay("2026-06-15", 23, 31, 10, 0.0, 1),
            KosForecastDay("2026-06-16", 22, 30, 20, 0.2, 2),
            KosForecastDay("2026-06-17", 21, 29, 45, 1.4, 61)
        )

        assertEquals(
            listOf("Mo 23/31°", "Di 22/30°", "Mi 21/29°"),
            forecast.map { WeatherReportFormatter.compactForecastLabel(it) }
        )
    }
}
