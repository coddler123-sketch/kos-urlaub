package com.example.kosurlaub

import com.example.kosurlaub.ui.home.today.CheckInWindow
import com.example.kosurlaub.ui.home.today.CheckInWindow.State
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckInWindowTest {
    private val hour = 3_600_000L
    private val dep = 100 * hour

    @Test fun waitingBeforeWindow() {
        val s = CheckInWindow.state(dep - 30 * hour, dep, 24) as State.Waiting
        assertEquals(6 * hour, s.remainingMillis)
    }

    @Test fun openInsideWindow() = assertTrue(CheckInWindow.state(dep - 2 * hour, dep, 24) is State.Open)

    @Test fun goneAfterDeparture() = assertTrue(CheckInWindow.state(dep, dep, 24) is State.Gone)

    @Test fun labels() {
        assertEquals("2 Tage 3 Std", CheckInWindow.remainingLabel(51 * hour))
        assertEquals("5 Std 10 Min", CheckInWindow.remainingLabel(5 * hour + 10 * 60_000L))
    }
}
