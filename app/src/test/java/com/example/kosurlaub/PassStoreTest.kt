package com.example.kosurlaub

import com.example.kosurlaub.ui.lists.Pass
import com.example.kosurlaub.ui.lists.PassStore
import com.example.kosurlaub.ui.lists.PassTiming
import org.junit.Assert.assertEquals
import org.junit.Test

class PassStoreTest {
    @Test fun roundTrip() {
        val items = listOf(Pass("a", "Hin · Alex", false), Pass("b", "Rück", true))
        assertEquals(items, PassStore.decode(PassStore.encode(items)))
    }

    @Test fun roundTripKeepsBarcodePayload() {
        val items = listOf(Pass("a", "Hin", true, "M1MUELLER/ANNA       EABC123 HAMKGSDE 1640 256Y012A0001 100^äß", "AZTEC"))
        assertEquals(items, PassStore.decode(PassStore.encode(items)))
    }

    @Test fun labelSkipsEmptyName() {
        assertEquals("Hin", PassStore.label("Hin", " "))
        assertEquals("Rück · Kim", PassStore.label("Rück", " Kim "))
    }

    @Test fun passBecomesPastAfterDeparture() {
        val out = Pass("a", "Hin · Anna", false)
        val back = Pass("b", "Rück", false)
        val beforeOut = TripData.outbound.departureMillis() - 1
        val afterOut = TripData.outbound.departureMillis() + 1
        assertEquals(false, PassTiming.isPast(out, beforeOut))
        assertEquals(true, PassTiming.isPast(out, afterOut))
        assertEquals(false, PassTiming.isPast(back, afterOut))
        assertEquals(true, PassTiming.isPast(back, TripData.inbound.departureMillis() + 1))
    }

    @Test fun barcodeRouteBeatsLabel() {
        val payload = "M1MUELLER/ANNA MRS    EN7K4Q2 KGSHAMDE 1641 271Y012A0001 100"
        val back = Pass("b", "Hin", false, payload, "AZTEC")
        assertEquals(TripData.inbound.departureMillis(), PassTiming.departureMillis(back))
    }

    @Test fun legAndLabelComeFromBarcode() {
        val payload = "M1MUELLER/KIM MRS     EN7K4Q2 HAMKGSDE 1640 256Y012B0001 100"
        assertEquals("Hin · Kim Mueller", PassTiming.autoLabel(payload))
        assertEquals("Rück", PassTiming.leg(Pass("x", "irgendwas", false, "M1MUELLER/KIM MRS     EN7K4Q2 KGSHAMDE 1641 271Y012B0001 100", "AZTEC")))
        assertEquals(null, PassTiming.autoLabel("https://example.com"))
    }
}
