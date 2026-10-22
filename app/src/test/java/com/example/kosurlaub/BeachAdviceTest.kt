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

    @Test fun unknownDirectionStillAdvises() = assertTrue(BeachAdvice.advice(30, null).isNotEmpty())
}
