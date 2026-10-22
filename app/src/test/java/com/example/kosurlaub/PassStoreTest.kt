package com.example.kosurlaub

import com.example.kosurlaub.ui.lists.Pass
import com.example.kosurlaub.ui.lists.PassStore
import org.junit.Assert.assertEquals
import org.junit.Test

class PassStoreTest {
    @Test fun roundTrip() {
        val items = listOf(Pass("a", "Hin · Alex", false), Pass("b", "Rück", true))
        assertEquals(items, PassStore.decode(PassStore.encode(items)))
    }

    @Test fun labelSkipsEmptyName() {
        assertEquals("Hin", PassStore.label("Hin", " "))
        assertEquals("Rück · Kim", PassStore.label("Rück", " Kim "))
    }
}
