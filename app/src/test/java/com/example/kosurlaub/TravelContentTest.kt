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

class TravelContentTest {
    @Test
    fun `all travel links use trusted https sources`() {
        val urls = travelSources.map { it.url } +
            ferryAndEventSources.map { it.url } +
            excursionIdeas.map { it.url }

        assertTrue(urls.all(::isTrustedTravelUrl))
    }

    @Test
    fun `bus section lists the Kos source only`() {
        assertEquals(
            setOf("www.kos.gr"),
            travelSources.map { java.net.URI(it.url).host }.toSet()
        )
    }

    @Test
    fun `untrusted and insecure links are rejected`() {
        assertFalse(isTrustedTravelUrl("http://www.example.com/schedule/"))
        assertFalse(isTrustedTravelUrl("https://example.com/"))
        assertFalse(isTrustedTravelUrl("not-a-url"))
    }

    @Test
    fun `selection toggles without mutating original set`() {
        val original = setOf("kosStadt")

        assertEquals(emptySet<String>(), toggledSelection(original, "kosStadt"))
        assertEquals(setOf("kosStadt", "marmari"), toggledSelection(original, "marmari"))
        assertEquals(setOf("kosStadt"), original)
    }
}
