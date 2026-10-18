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

class CountdownMilestoneTest {
    @Test
    fun `milestones exist at configured days`() {
        assertEquals("Ein Monat bis Kos", countdownMilestone(30)?.message)
        assertEquals("In zwei Wochen geht es los", countdownMilestone(14)?.message)
        assertEquals("Nur noch eine Woche", countdownMilestone(7)?.message)
        assertEquals("Morgen geht es nach Kos", countdownMilestone(1)?.message)
    }

    @Test
    fun `ordinary countdown day has no milestone`() {
        assertNull(countdownMilestone(69))
    }
}
