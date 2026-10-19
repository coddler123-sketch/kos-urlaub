package com.example.kosurlaub

import com.example.kosurlaub.ui.compass.compassTips
import com.example.kosurlaub.ui.compass.matchesCompassQuickFilter
import com.example.kosurlaub.ui.compass.quickFilterGroups
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CompassQuickFilterTest {

    private val filters = quickFilterGroups.flatMap { it.filters }

    @Test
    fun everyFilterMatchesAtLeastOneTip() {
        filters.forEach { filter ->
            assertTrue("${filter.label} trifft keinen Tipp", compassTips.any { matchesCompassQuickFilter(it, filter.key) })
        }
    }

    @Test
    fun keysAreUnique() {
        assertEquals(filters.size, filters.map { it.key }.toSet().size)
    }

    @Test
    fun labelsHaveNoEmojiAndNoDuplicatesOfCategories() {
        filters.forEach { assertTrue(it.label, it.label.none { c -> Character.getType(c) == Character.OTHER_SYMBOL.toInt() }) }
        assertTrue(filters.none { it.label == "Essen" || it.label == "Rund ums Hotel" })
    }
}
