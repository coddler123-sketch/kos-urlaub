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

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class CountdownCalculatorTest {

    private fun getMockToday(year: Int, month: Int, day: Int, hour: Int = 12, minute: Int = 0): Calendar {
        return Calendar.getInstance(java.util.TimeZone.getTimeZone("Europe/Berlin")).apply {
            clear()
            set(year, month, day, hour, minute, 0)
        }
    }

    @Test
    fun `daysUntilDeparture returns positive before departure`() {
        val today = getMockToday(2026, Calendar.JUNE, 15)
        val days = CountdownCalculator.daysUntilDeparture(today)
        assertTrue("Expected positive days, got $days", days > 0)
    }

    @Test
    fun `calculate returns non-negative time components`() {
        val today = getMockToday(2026, Calendar.JUNE, 15)
        val r = CountdownCalculator.calculate(today)
        assertTrue(r.days >= 0)
        assertTrue(r.hours in 0..23)
        assertTrue(r.minutes in 0..59)
        assertTrue(r.seconds in 0..59)
    }

    @Test
    fun `isReached is false before departure day`() {
        val today = getMockToday(2027, Calendar.SEPTEMBER, 12, 23, 59)
        val r = CountdownCalculator.calculate(today)
        assertFalse(r.isReached)
    }

    @Test
    fun `isOnVacation is false before departure`() {
        val today = getMockToday(2027, Calendar.SEPTEMBER, 13, 13, 24)
        val r = CountdownCalculator.calculate(today)
        assertFalse(r.isOnVacation)
    }

    @Test
    fun `progressFraction is between 0 and 1`() {
        val today = getMockToday(2026, Calendar.JUNE, 15)
        val p = CountdownCalculator.progressFraction(today)
        assertTrue("progressFraction=$p out of range", p in 0f..1f)
    }

    @Test
    fun `daysUntilDeparture matches calculate days roughly`() {
        val today = getMockToday(2026, Calendar.JUNE, 15)
        val days = CountdownCalculator.daysUntilDeparture(today).toLong()
        val r = CountdownCalculator.calculate(today)
        assertTrue(
            "daysUntilDeparture=$days vs calculate.days=${r.days}",
            kotlin.math.abs(days - r.days) <= 1
        )
    }

    @Test
    fun `isReached is true on departure day but isOnVacation is false before arrival`() {
        val departureDayNoon = getMockToday(2027, Calendar.SEPTEMBER, 13, 15, 0)
        val r = CountdownCalculator.calculate(departureDayNoon)
        assertTrue(r.isReached)
        assertFalse(r.isOnVacation)
    }

    @Test
    fun `isOnVacation is false before arrival in Kos`() {
        val beforeArrival = getMockToday(2027, Calendar.SEPTEMBER, 13, 16, 44)
        assertFalse(CountdownCalculator.calculate(beforeArrival).isOnVacation)
    }

    @Test
    fun `isOnVacation is true after arrival in Kos`() {
        val afterArrival = getMockToday(2027, Calendar.SEPTEMBER, 13, 16, 46)
        val r = CountdownCalculator.calculate(afterArrival)
        assertTrue(r.isReached)
        assertTrue(r.isOnVacation)
    }

    @Test
    fun `trip is over after the return flight departs`() {
        val before = CountdownCalculator.calculate(getMockToday(2027, Calendar.SEPTEMBER, 28, 9, 0))
        assertTrue(before.isOnVacation)
        assertFalse(before.isOver)
        val after = CountdownCalculator.calculate(getMockToday(2027, Calendar.SEPTEMBER, 29, 9, 0))
        assertTrue(after.isOver)
        assertFalse(after.isOnVacation)
    }

    @Test
    fun `midnight zeroes out time fields`() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 45)
            set(Calendar.MILLISECOND, 999)
        }
        val result = CountdownCalculator.midnight(cal)
        assertEquals(0, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, result.get(Calendar.MINUTE))
        assertEquals(0, result.get(Calendar.SECOND))
        assertEquals(0, result.get(Calendar.MILLISECOND))
    }
}
