package com.example.kosurlaub

import com.example.kosurlaub.ui.home.HomeLayout
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeLayoutTest {
    @Test fun savedOrderWinsNewCardsAppend() {
        assertEquals(listOf("b", "a", "c"), HomeLayout.order(listOf("a", "b", "c"), listOf("b", "a", "gone")))
    }

    @Test fun moveSwapsAndStopsAtEdges() {
        val o = listOf("a", "b", "c")
        assertEquals(listOf("b", "a", "c"), HomeLayout.move(o, "a", 1))
        assertEquals(o, HomeLayout.move(o, "a", -1))
        assertEquals(o, HomeLayout.move(o, "c", 1))
    }
}
