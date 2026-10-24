package com.example.kosurlaub

import com.example.kosurlaub.ui.weather.BeachAdvice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BeachAdviceTest {
    @Test fun directionNames() {
        assertEquals("Nord", BeachAdvice.directionName(0))
        assertEquals("Nord", BeachAdvice.directionName(350))
        assertEquals("Nordost", BeachAdvice.directionName(45))
        assertEquals("Süd", BeachAdvice.directionName(180))
        assertEquals("Nordwest", BeachAdvice.directionName(315))
    }

    @Test fun calmWindIsAlwaysGood() = assertTrue(BeachAdvice.advice(8, 0).startsWith("Kaum Wind"))

    @Test fun strongNorthWindPointsSouth() = assertTrue(BeachAdvice.advice(30, 10).contains("Südstrände"))

    @Test fun southWindKeepsNorthCoast() = assertTrue(BeachAdvice.advice(30, 180).contains("Nordküste ist meist geschützt"))

    @Test fun rainAddsNote() = assertTrue(BeachAdvice.advice(8, 0, 25, 1.2).startsWith("Es regnet"))

    @Test fun coolDayAddsNote() = assertTrue(BeachAdvice.advice(8, 0, 17, 0.0).startsWith("Heute wird es nur bis 17°"))

    @Test fun coolMorningButWarmDayHasNoNote() = assertTrue(BeachAdvice.advice(8, 0, 17, 0.0, todayMaxCelsius = 26).startsWith("Kaum Wind"))

    @Test fun likelyShowersAddNote() = assertTrue(BeachAdvice.advice(8, 0, 25, 0.0, rainProbabilityPercent = 60).startsWith("Heute sind Schauer"))

    @Test fun unlikelyShowersAddNothing() = assertTrue(BeachAdvice.advice(8, 0, 25, 0.0, rainProbabilityPercent = 20).startsWith("Kaum Wind"))

    @Test fun coldSeaAddsNote() = assertTrue(BeachAdvice.advice(8, 0, 25, 0.0, seaCelsius = 18.4).startsWith("Das Meer hat erst 18°"))

    @Test fun warmDryHasNoNote() = assertTrue(BeachAdvice.advice(8, 0, 28, 0.0).startsWith("Kaum Wind"))

    @Test fun unknownDirectionStillAdvises() = assertTrue(BeachAdvice.advice(30, null).isNotEmpty())
}
