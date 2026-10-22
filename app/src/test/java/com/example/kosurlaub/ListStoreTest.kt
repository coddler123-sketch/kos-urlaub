package com.example.kosurlaub

import com.example.kosurlaub.ui.lists.ListEntry
import com.example.kosurlaub.ui.lists.ListStore
import org.junit.Assert.assertEquals
import org.junit.Test

class ListStoreTest {
    @Test
    fun roundTripKeepsFieldsAndDoneFlag() {
        val items = listOf(ListEntry("Koffer", "Hut", "", true), ListEntry("", "Geld", "Für Bus", false))
        assertEquals(items, ListStore.decode(ListStore.encode(items)))
    }

    @Test
    fun separatorsInTextAreCleaned() {
        val decoded = ListStore.decode(ListStore.encode(listOf(ListEntry("", "a\tb\nc", "", false))))
        assertEquals("a b c", decoded.single().text)
    }

    @Test
    fun progressCountsDone() {
        assertEquals(1 to 2, ListStore.progress(listOf(ListEntry("", "a", "", true), ListEntry("", "b", "", false))))
    }

    @Test
    fun seedsAreNotEmpty() {
        assertEquals(true, ListStore.packingSeed.isNotEmpty() && ListStore.tasksSeed.isNotEmpty())
    }
}
