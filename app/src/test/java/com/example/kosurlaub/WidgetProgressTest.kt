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

class WidgetProgressTest {

    @Test fun `widget progress maps fraction to progress bar range`() {
        assertEquals(0, KosCountdownLargeWidgetProvider.widgetProgressValue(0f))
        assertEquals(500, KosCountdownLargeWidgetProvider.widgetProgressValue(0.5f))
        assertEquals(1000, KosCountdownLargeWidgetProvider.widgetProgressValue(1f))
    }

    @Test fun `widget progress clamps out of range fractions`() {
        assertEquals(0, KosCountdownLargeWidgetProvider.widgetProgressValue(-0.25f))
        assertEquals(1000, KosCountdownLargeWidgetProvider.widgetProgressValue(1.25f))
    }

    @Test fun `decorative phrase is hidden for large system text`() {
        assertEquals(true, KosCountdownLargeWidgetProvider.shouldShowWidgetPhrase(1.0f))
        assertEquals(false, KosCountdownLargeWidgetProvider.shouldShowWidgetPhrase(1.3f))
        assertEquals(false, KosCountdownLargeWidgetProvider.shouldShowWidgetPhrase(1.5f))
    }
}
