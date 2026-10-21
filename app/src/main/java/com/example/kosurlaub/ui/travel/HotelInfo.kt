package com.example.kosurlaub.ui.travel

import com.example.kosurlaub.ui.compass.CompassTipDetail

/** Hotel auf einen Blick, aus vtours, Corendon, Hotelmix und der Hotel-Website. */
internal val hotelDetails = listOf(
        CompassTipDetail("LAGE", "Ioanni Kapodistria 82, Tigaki, 85300 Kos (laut Bing Maps). Sandstrand und Ortszentrum ca. 0,9–1,1 km entfernt, Liegen und Schirme am Strand gegen Gebühr. Asklepieion und Zia ca. 12 km, Therma ca. 25 km."),
        CompassTipDetail("EUER ZIMMER", "Doppelzimmer mit Klimaanlage, ca. 20 m², Bad oder Dusche, Balkon oder Terrasse, Kühlschrank, Wasserkocher, Fön und 32-Zoll-TV mit Sat-Programmen. Kostenloses WLAN auch im Zimmer. Zimmerreinigung 6-mal pro Woche, Handtücher und Bettwäsche werden 3-mal pro Woche gewechselt. Safe, Bügeleisen und Wäscheservice kosten extra (laut Hotel-Website)."),
        CompassTipDetail("ALL INCLUSIVE", "Buffet: Frühstück ca. 07:30–09:30/09:45, Mittagessen 12:30–14:00, Kaffee und Kuchen 16:00–17:00, Abendessen 19:00–21:00. Lokale Getränke an der Bar 10:00–23:00. Nicht enthalten: Flaschen- und importierte Getränke, Spirituosen, Cocktails, frische Säfte."),
        CompassTipDetail("ANLAGE", "2 Außenpools mit Kinderbecken (Liegen und Schirme inklusive), Whirlpool, Poolbar, Spielplatz, 24-Stunden-Rezeption, Aufzug, Minimarkt, kostenloses WLAN. Eigene Farm, deren Gemüse und Obst in der Küche landet. Frühstücksterrasse mit Blick aufs Meer und den Dikeos. Der Salzsee von Tigaki und Radwege liegen in der Nähe. Gegen Gebühr: Tennis, Billard, Fitness, Fahrradverleih, Flughafen-Shuttle."),
        CompassTipDetail("CHECK-IN / CHECK-OUT", "Check-in ab 15:00 Uhr (Ankunft 13.09. um 17:45 Uhr passt), Check-out bis 11:00 Uhr. Der Rückflug am 28.09. geht um 11:35 Uhr – den Transfer zum Flughafen früh klären."),
        CompassTipDetail("KONTAKT", "Telefon +30 22420 69411 oder +30 22420 69412 (laut Hotel-Website), E-Mail info@ilioshotel.com."),
        CompassTipDetail("VOR ORT ZAHLEN", "Klimaresilienz-Abgabe laut vtours ca. 5 € pro Zimmer und Nacht (April–Oktober), Änderungen vorbehalten."),
        CompassTipDetail("UNSICHER", "Zahl der Zimmer (130 bzw. 151), Entfernung zur Bushaltestelle (bis 50 m bzw. über 400 m) und Parkplatz (gebührenpflichtig bzw. kostenlos) werden uneinheitlich angegeben.")
)

internal const val HOTEL_REVIEW_SUMMARY = "Gäste bewerten die Anlage mit 8,3 von 10 (Corendon, 21 Bewertungen) bzw. 8,4 von 10 (Hotelmix inkl. Booking.com, 136 Bewertungen). Gelobt werden Gastfreundlichkeit und Ruhe; die Lage (7,8) und das WLAN (7,4) schneiden am schwächsten ab."

internal const val HOTEL_SOURCE_NOTE = "Angaben aus vtours, Corendon und Hotelmix – bei Abweichungen gelten Reisevoucher und Rezeption"
