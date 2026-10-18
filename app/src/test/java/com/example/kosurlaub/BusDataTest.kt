package com.example.kosurlaub

import com.example.kosurlaub.ui.travel.KtelKosTimetable
import org.junit.Assert.assertTrue
import org.junit.Test

class BusDataTest {
    private val time = Regex("""([01]\d|2[0-3]):[0-5]\d""")

    @Test
    fun `every route has https source, stand and sorted valid times`() {
        KtelKosTimetable.routes.forEach { route ->
            assertTrue(route.source.startsWith("https://"))
            assertTrue(route.asOf.isNotBlank())
            assertTrue(route.departures.isNotEmpty())
            route.departures.forEach { assertTrue("Zeit ungültig: ${it.time}", time.matches(it.time)) }
            val times = route.departures.map { it.time }
            assertTrue("Zeiten nicht sortiert in ${route.from} → ${route.to}", times == times.sorted())
        }
    }

    @Test
    fun `official site is https`() {
        assertTrue(KtelKosTimetable.OFFICIAL_URL.startsWith("https://"))
    }
}
