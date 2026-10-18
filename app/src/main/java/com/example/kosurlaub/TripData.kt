package com.example.kosurlaub

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** Einzige Quelle für alle Reisedaten. Alle Uhrzeiten sind Ortszeiten des jeweiligen Flughafens. */
object TripData {
    data class Flight(
        val airline: String,
        val number: String,
        val weekday: String,
        val year: Int,
        val month: Int,
        val day: Int,
        val fromCity: String,
        val fromAirport: String,
        val fromZone: String,
        val departHour: Int,
        val departMinute: Int,
        val toCity: String,
        val toAirport: String,
        val toZone: String,
        val arriveHour: Int,
        val arriveMinute: Int
    ) {
        fun departureMillis(): Long = millis(fromZone, year, month, day, departHour, departMinute)
        fun arrivalMillis(): Long = millis(toZone, year, month, day, arriveHour, arriveMinute)

        fun summary(): String = String.format(
            Locale.GERMAN,
            "%s %02d.%02d.%d · %s %02d:%02d → %s %02d:%02d (Ortszeit) · %s %s",
            weekday, day, month, year,
            fromCity, departHour, departMinute,
            toCity, arriveHour, arriveMinute,
            airline, number
        )
    }

    val outbound = Flight(
        airline = "Condor", number = "DE1640", weekday = "Mo",
        year = 2027, month = 9, day = 13,
        fromCity = "Hamburg", fromAirport = "HAM", fromZone = "Europe/Berlin",
        departHour = 13, departMinute = 25,
        toCity = "Kos", toAirport = "KGS", toZone = "Europe/Athens",
        arriveHour = 17, arriveMinute = 45
    )

    val inbound = Flight(
        airline = "Condor", number = "DE1641", weekday = "Di",
        year = 2027, month = 9, day = 28,
        fromCity = "Kos", fromAirport = "KGS", fromZone = "Europe/Athens",
        departHour = 11, departMinute = 35,
        toCity = "Hamburg", toAirport = "HAM", toZone = "Europe/Berlin",
        arriveHour = 14, arriveMinute = 0
    )

    const val CHECKED_BAG_KG = 23
    const val CARRY_ON_KG = 8
    const val CARRY_ON_SIZE = "40 x 55 x 20 cm"
    const val OPERATOR = "ByeBye"
    const val ROOM = "Doppelzimmer mit Klimaanlage, Bad oder Dusche, Balkon oder Terrasse"
    const val BOARD = "All Inclusive"

    const val HOTEL_NAME = "ILIOS.K Village Resort"
    const val HOTEL_PLACE = "Tigaki"
    const val HOTEL_POSTAL_CODE = "85300"
    const val HOTEL_LAT = 36.8800
    const val HOTEL_LON = 27.1834
    const val HOTEL_CHECK_IN = "ab 15:00 Uhr"
    const val HOTEL_CHECK_OUT = "bis 11:00 Uhr"

    /** Nur auf top-hotels-gr.com gefunden, nicht gegengeprüft (Vorwahl 22420 passt zu Kos). */
    const val HOTEL_PHONE_UNCONFIRMED = "+30 22420 69411"

    private fun millis(zone: String, y: Int, m: Int, d: Int, h: Int, min: Int): Long =
        Calendar.getInstance(TimeZone.getTimeZone(zone)).apply {
            clear()
            set(y, m - 1, d, h, min, 0)
        }.timeInMillis
}
