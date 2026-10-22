package com.example.kosurlaub

import com.example.kosurlaub.ui.lists.Bcbp
import com.example.kosurlaub.ui.lists.BoardingData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BcbpTest {
    // Beispiel aus der IATA-Spezifikation (Resolution 792).
    private val sample = "M1DESMARAIS/LUC       EABC123 YULFRAAC 0834 226F001A0025 100"

    @Test fun parsesMandatorySection() {
        assertEquals(
            BoardingData("Luc Desmarais", "ABC123", "YUL", "FRA", "AC 834", 226, "1A"),
            Bcbp.parse(sample)
        )
    }

    @Test fun rejectsNonBoardingText() {
        assertNull(Bcbp.parse("https://example.com"))
        assertNull(Bcbp.parse("M1SHORT"))
    }

    @Test fun nameWithTitleAndSwap() = assertEquals("Anna Mueller", Bcbp.prettyName("MUELLER/ANNA MRS"))

    @Test fun dayOfYearToDate() {
        assertEquals("13.09.", Bcbp.dayMonth(256, 2027))
        assertEquals("28.09.", Bcbp.dayMonth(271, 2027))
    }
}
