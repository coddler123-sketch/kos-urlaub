package com.example.kosurlaub

import com.example.kosurlaub.ui.travel.HOTEL_REVIEW_SUMMARY
import com.example.kosurlaub.ui.travel.hotelDetails
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
import org.junit.Assert.assertTrue
import org.junit.Test

class CompassTipsTest {
    @Test
    fun `tip collection has one stable id per tip`() {
        assertEquals(25, compassTips.size)
        assertEquals(compassTips.size, compassTips.map { it.id }.distinct().size)
        assertTrue(compassTips.all { it.id.isNotBlank() && it.id != it.title })
        assertTrue(compassTips.all { it.source == CompassTipSource.RESEARCHED })
        assertTrue(compassTips.any { it.category == "Tigaki" })
    }

    @Test
    fun `search matches title location journey and tags`() {
        assertTrue(filterCompassTips(compassTips, "Asklepieion").any { it.title.contains("Asklepieion") })
        assertTrue(filterCompassTips(compassTips, "Tigaki").size >= 3)
        assertTrue(filterCompassTips(compassTips, "ohne Auto").isNotEmpty())
        assertEquals(
            listOf("Bus ab Tigaki nach Kos-Stadt (KTEL Kos)"),
            filterCompassTips(compassTips, "KTEL", "Mobilität").map { it.title }
        )
    }

    @Test
    fun `researched tips that link out use secure sources`() {
        val linkedTips = compassTips.filter { it.sourceUrl != null }

        assertTrue(linkedTips.isNotEmpty())
        assertTrue(linkedTips.all { it.sourceUrl?.startsWith("https://") == true })
    }

    @Test
    fun `CSV covers every tip id exactly once`() {
        assertEquals(compassTipIds.toSet(), generatedCompassTipIds)
        assertEquals(compassTipIds.size, compassTipIds.toSet().size)
    }

    @Test
    fun `hotel info carries sourced details`() {
        assertTrue(hotelDetails.any { it.title == "ALL INCLUSIVE" })
        assertTrue(hotelDetails.any { it.title == "KONTAKT" })
        assertTrue(HOTEL_REVIEW_SUMMARY.isNotBlank())
    }

    @Test
    fun `featured tip rotates predictably`() {
        assertEquals(compassTips.first(), featuredCompassTip(compassTips, 1))
        assertEquals(compassTips.first(), featuredCompassTip(compassTips, compassTips.size + 1))
    }

    @Test
    fun `day plan contains at most three saved tips`() {
        val saved = compassTips.take(5).mapTo(mutableSetOf()) { it.id }

        val plan = buildCompassDayPlan(compassTips, saved)

        assertEquals(3, plan.size)
        assertTrue(plan.all { it.id in saved })
    }

    @Test
    fun `quick filters use editorial travel details`() {
        val busTip = compassTips.first { it.title == "Bus ab Tigaki nach Kos-Stadt (KTEL Kos)" }
        val food = compassTips.first { it.category == "Essen" }

        assertTrue(matchesCompassQuickFilter(busTip, "15–30 Min Bus/Taxi"))
        assertTrue(matchesCompassQuickFilter(food, "Essen"))
    }

    @Test
    fun `legacy titles migrate to stable ids without changing unknown values`() {
        val titleToId = compassTips.associate { it.title to it.id }
        val migrated = migrateCompassSelection(
            setOf("Tigaki Beach direkt vor Ort", "future-stable-id"),
            titleToId
        )

        assertEquals(setOf("beach-tigaki", "future-stable-id"), migrated)
    }
}
