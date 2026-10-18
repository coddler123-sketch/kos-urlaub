package com.example.kosurlaub

import com.example.kosurlaub.ui.home.ChecklistItem
import com.example.kosurlaub.ui.home.ChecklistStore
import org.junit.Assert.assertEquals
import org.junit.Test

class ChecklistStoreTest {
    @Test
    fun roundTripKeepsTextAndState() {
        val items = listOf(ChecklistItem("Pässe | Perso", true), ChecklistItem("Sonnencreme", false))
        assertEquals(items, ChecklistStore.decode(ChecklistStore.encode(items)))
    }

    @Test
    fun newlinesBecomeSpacesAndEmptyInputIsEmptyList() {
        assertEquals("0|a b", ChecklistStore.encode(listOf(ChecklistItem("a\nb", false))))
        assertEquals(emptyList<ChecklistItem>(), ChecklistStore.decode(null))
        assertEquals(emptyList<ChecklistItem>(), ChecklistStore.decode(""))
    }
}
