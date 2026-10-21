package com.example.kosurlaub

import com.example.kosurlaub.ui.home.phraseOfTheDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DailyPhraseTest {
    private val countdown = arrayOf("c0", "c1", "c2")
    private val daily = arrayOf("d0", "d1", "d2", "d3")

    @Test
    fun `last days use the matching countdown phrase`() {
        assertEquals("c2", phraseOfTheDay(countdown, daily, 0, 100))
        assertEquals("c1", phraseOfTheDay(countdown, daily, 1, 100))
        assertEquals("c0", phraseOfTheDay(countdown, daily, 2, 100))
    }

    @Test
    fun `earlier days rotate through the daily pool`() {
        assertEquals("d0", phraseOfTheDay(countdown, daily, 331, 20_000))
        assertEquals("d1", phraseOfTheDay(countdown, daily, 330, 20_001))
        assertNotEquals(
            phraseOfTheDay(countdown, daily, 331, 20_000),
            phraseOfTheDay(countdown, daily, 330, 20_001)
        )
        assertEquals("d0", phraseOfTheDay(countdown, daily, 327, 20_004))
    }

    @Test
    fun `far ahead uses the far pool, closer days the daily pool`() {
        val far = arrayOf("f0", "f1")
        assertEquals("f0", phraseOfTheDay(countdown, daily, 300, 20_000, far))
        assertEquals("f1", phraseOfTheDay(countdown, daily, 300, 20_001, far))
        assertEquals("d0", phraseOfTheDay(countdown, daily, 200, 20_000, far))
    }

    @Test
    fun `after the departure the last countdown phrase stays`() {
        assertEquals("c2", phraseOfTheDay(countdown, daily, -1, 20_000))
    }
}
