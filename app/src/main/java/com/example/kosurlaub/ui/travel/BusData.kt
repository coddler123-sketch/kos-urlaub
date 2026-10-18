package com.example.kosurlaub.ui.travel

internal data class BusDeparture(val time: String, val note: String = "")

internal data class BusRoute(
    val from: String,
    val to: String,
    val validity: String,
    val departures: List<BusDeparture>,
    val source: String,
    val asOf: String
)

/** Fahrplan von KTEL Kos. Nur mit Quelle und Stand eintragen; leer heißt: nicht verifiziert. */
internal object KtelKosTimetable {
    const val OFFICIAL_URL = "https://www.ktel-kos.gr/"

    private const val SOURCE_URL = "https://ktel-kos.gr/dromologia/"
    private const val AS_OF = "16.10.2026"

    val routes: List<BusRoute> = listOf(
        BusRoute(
            from = "Kos-Stadt", to = "Tigaki",
            validity = "Mo–Sa · Referenz: gültig ab 28.09.2026, Saison nicht angegeben; Fahrplan für September 2027 noch nicht veröffentlicht · Preis 2,50 €",
            departures = listOf(BusDeparture("09:00"), BusDeparture("10:15"), BusDeparture("11:15"), BusDeparture("12:15"), BusDeparture("13:00"), BusDeparture("15:00"), BusDeparture("16:00"), BusDeparture("17:30"), BusDeparture("19:00"), BusDeparture("20:30"), BusDeparture("21:45")),
            source = SOURCE_URL,
            asOf = AS_OF
        ),
        BusRoute(
            from = "Tigaki", to = "Kos-Stadt",
            validity = "Mo–Sa · Referenz: gültig ab 28.09.2026, Saison nicht angegeben; Fahrplan für September 2027 noch nicht veröffentlicht · Preis 2,50 €",
            departures = listOf(BusDeparture("08:10"), BusDeparture("09:30"), BusDeparture("10:45"), BusDeparture("11:45"), BusDeparture("12:45"), BusDeparture("15:30"), BusDeparture("16:30"), BusDeparture("18:00"), BusDeparture("19:30"), BusDeparture("21:00"), BusDeparture("22:15")),
            source = SOURCE_URL,
            asOf = AS_OF
        ),
        BusRoute(
            from = "Kos-Stadt", to = "Tigaki",
            validity = "Sonntag · Referenz: gültig ab 28.09.2026, Saison nicht angegeben; Fahrplan für September 2027 noch nicht veröffentlicht · Preis 2,50 €",
            departures = listOf(BusDeparture("10:15"), BusDeparture("11:30"), BusDeparture("13:00"), BusDeparture("15:00"), BusDeparture("16:30"), BusDeparture("18:00"), BusDeparture("19:30"), BusDeparture("21:00")),
            source = SOURCE_URL,
            asOf = AS_OF
        ),
        BusRoute(
            from = "Tigaki", to = "Kos-Stadt",
            validity = "Sonntag · Referenz: gültig ab 28.09.2026, Saison nicht angegeben; Fahrplan für September 2027 noch nicht veröffentlicht · Preis 2,50 €",
            departures = listOf(BusDeparture("09:30"), BusDeparture("10:45"), BusDeparture("12:00"), BusDeparture("13:30"), BusDeparture("15:30"), BusDeparture("17:00"), BusDeparture("18:30"), BusDeparture("20:00"), BusDeparture("21:30")),
            source = SOURCE_URL,
            asOf = AS_OF
        )
    )

    val isVerified: Boolean get() = routes.isNotEmpty()
}
