package com.example.kosurlaub.ui.compass
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

// Hinweis: Diese Liste ist bewusst kleiner als die Vorlage fuer Kos.
// Konkrete Lokalempfehlungen (Restaurants, Laeden) fuer Tigaki/Kos sind nicht
// verifiziert und deshalb hier nicht erfunden - bei Bedarf vor Ort oder nach
// eigener Recherche ergaenzen.
internal val compassTips = listOf(
    CompassTip(
        category = "Unterkünfte",
        title = "Ilios K. Village – unser Hotel",
        description = "4-Sterne-Anlage in Tigaki an der Nordküste von Kos, deutschsprachig geführt, rund 1 km vom Sandstrand und Ortszentrum entfernt.",
        note = "Angaben aus vtours, Corendon und Hotelmix – bei Abweichungen gelten Reisevoucher und Rezeption",
        kind = CompassTipKind.NOTE,
        location = "Tigaki, Kos",
        journey = "Vor Ort; Flughafen Kos (KGS) ca. 13 km Luftlinie (berechnet), Transfer ca. 20–30 Minuten",
        tags = listOf("Tigaki", "Hotel", "All Inclusive", "Pool"),
        reviewSummary = "Gäste bewerten die Anlage mit 8,3 von 10 (Corendon, 21 Bewertungen) bzw. 8,4 von 10 (Hotelmix inkl. Booking.com, 136 Bewertungen). Gelobt werden Gastfreundlichkeit und Ruhe; die Lage (7,8) und das WLAN (7,4) schneiden am schwächsten ab.",
        hotelDetails = listOf(
            CompassTipDetail("LAGE", "Ioanni Kapodistria 82, Tigaki, 85300 Kos (laut Bing Maps). Sandstrand und Ortszentrum ca. 0,9–1,1 km entfernt, Liegen und Schirme am Strand gegen Gebühr. Asklepieion und Zia ca. 12 km, Therma ca. 25 km."),
            CompassTipDetail("EUER ZIMMER", "Doppelzimmer mit Klimaanlage, ca. 20 m², Bad oder Dusche, Balkon oder Terrasse, Kühlschrank, Wasserkocher, Fön und 32-Zoll-TV mit Sat-Programmen. Kostenloses WLAN auch im Zimmer. Zimmerreinigung 6-mal pro Woche, Handtücher und Bettwäsche werden 3-mal pro Woche gewechselt. Safe, Bügeleisen und Wäscheservice kosten extra (laut Hotel-Website)."),
            CompassTipDetail("ALL INCLUSIVE", "Buffet: Frühstück ca. 07:30–09:30/09:45, Mittagessen 12:30–14:00, Kaffee und Kuchen 16:00–17:00, Abendessen 19:00–21:00. Lokale Getränke an der Bar 10:00–23:00. Nicht enthalten: Flaschen- und importierte Getränke, Spirituosen, Cocktails, frische Säfte."),
            CompassTipDetail("ANLAGE", "2 Außenpools mit Kinderbecken (Liegen und Schirme inklusive), Whirlpool, Poolbar, Spielplatz, 24-Stunden-Rezeption, Aufzug, Minimarkt, kostenloses WLAN. Eigene Farm, deren Gemüse und Obst in der Küche landet. Frühstücksterrasse mit Blick aufs Meer und den Dikeos. Der Salzsee von Tigaki und Radwege liegen in der Nähe. Gegen Gebühr: Tennis, Billard, Fitness, Fahrradverleih, Flughafen-Shuttle."),
            CompassTipDetail("CHECK-IN / CHECK-OUT", "Check-in ab 15:00 Uhr (Ankunft 13.09. um 17:45 Uhr passt), Check-out bis 11:00 Uhr. Der Rückflug am 28.09. geht um 11:35 Uhr – den Transfer zum Flughafen früh klären."),
            CompassTipDetail("KONTAKT", "Telefon +30 22420 69411 oder +30 22420 69412 (laut Hotel-Website), E-Mail info@ilioshotel.com."),
            CompassTipDetail("VOR ORT ZAHLEN", "Klimaresilienz-Abgabe laut vtours ca. 5 € pro Zimmer und Nacht (April–Oktober), Änderungen vorbehalten."),
            CompassTipDetail("UNSICHER", "Zahl der Zimmer (130 bzw. 151), Entfernung zur Bushaltestelle (bis 50 m bzw. über 400 m) und Parkplatz (gebührenpflichtig bzw. kostenlos) werden uneinheitlich angegeben.")
        ),
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://www.vtours.com/de/reiseziele/griechenland/iliosk-village-resort-tigaki-KGS27364/",
    ),
    CompassTip(
        category = "Ausflüge",
        title = "Kos-Stadt: Burg, Hippokratesbaum & Hafen",
        description = "Die Inselhauptstadt vereint die Johanniterburg Nerantzia, die Platane des Hippokrates, die Casa Romana und einen lebhaften Hafen.",
        note = "Mit dem Bus ab Tigaki gut erreichbar",
        location = "Kos-Stadt",
        journey = "Bus ab Tigaki, ca. 20–30 Minuten",
        tags = listOf("Kultur", "Hafen", "Halbtag"),
        source = CompassTipSource.RESEARCHED
    ),
    CompassTip(
        category = "Ausflüge",
        title = "Asklepieion – antike Heilstätte",
        description = "Das Heiligtum des Asklepios liegt auf einem grünen Hügel wenige Kilometer von Kos-Stadt entfernt und gilt als eine der bedeutendsten archäologischen Stätten Griechenlands.",
        note = "Feste Schuhe wegen Treppen und unebenem Gelände",
        location = "bei Kos-Stadt",
        journey = "Von Kos-Stadt mit Stadtbus, Taxi oder Auto",
        tags = listOf("Kultur", "Geschichte", "Halbtag"),
        source = CompassTipSource.RESEARCHED
    ),
    CompassTip(
        category = "Ausflüge",
        title = "Zia – Sonnenuntergang im Bergdorf",
        description = "Das Bergdorf am Fuß des Dikeos gilt als beliebtester Aussichtspunkt der Insel für den Sonnenuntergang.",
        note = "Öffentliche Busverbindung nicht sicher bestätigt – am besten mit Mietwagen, Taxi oder organisierter Tour anreisen",
        kind = CompassTipKind.CAUTION,
        location = "Zia, Kos",
        journey = "Mietwagen, Taxi oder Tour",
        tags = listOf("Aussicht", "Abend", "Natur"),
        source = CompassTipSource.RESEARCHED
    ),
    CompassTip(
        category = "Ausflüge",
        title = "Festung Antimachia",
        description = "Die mittelalterliche Burg der Johanniterritter liegt in der Inselmitte nahe dem Flughafen und bietet einen weiten Blick über die Küste.",
        note = "Öffnungszeiten vor dem Besuch prüfen",
        location = "Antimachia, Kos",
        journey = "Mietwagen oder Taxi",
        tags = listOf("Geschichte", "Aussicht", "Halbtag"),
        source = CompassTipSource.RESEARCHED
    ),
    CompassTip(
        category = "Ausflüge",
        title = "Therme Embros bei Agios Fokas",
        description = "Eine natürlich warme Quelle direkt am Meer – ein beliebter Ort zum Baden im warmen Wasser.",
        note = "Zugang und Beschaffenheit können je nach Jahreszeit variieren",
        location = "Agios Fokas, bei Kos-Stadt",
        journey = "Mietwagen oder Taxi, meist in Kombination mit Kos-Stadt",
        tags = listOf("Baden", "Natur", "Halbtag"),
        source = CompassTipSource.RESEARCHED
    ),
    CompassTip(
        category = "Ausflüge",
        title = "Bootsausflug nach Nisyros",
        description = "Die benachbarte Vulkaninsel mit ihrem großen Krater ist ein beliebtes Tagesausflugsziel per Boot.",
        note = "Abfahrtsort (meist Kos-Stadt oder Kardamena) und Fahrzeiten vorab prüfen",
        kind = CompassTipKind.CAUTION,
        location = "Nisyros",
        journey = "Ausflugsboot, ganzer Tag",
        tags = listOf("Tagesausflug", "Boot", "Natur"),
        source = CompassTipSource.RESEARCHED
    ),
    CompassTip(
        category = "Ausflüge",
        title = "Tagesausflug nach Bodrum (Türkei)",
        description = "Von Kos aus fahren Ausflugsboote zur türkischen Stadt Bodrum mit ihren Basaren und der Burg.",
        note = "Reisedokumente und Einreisebestimmungen für die Türkei vorher unbedingt prüfen",
        kind = CompassTipKind.CAUTION,
        location = "Bodrum, Türkei",
        journey = "Ausflugsboot, ganzer Tag",
        tags = listOf("Tagesausflug", "Boot", "Ausland"),
        source = CompassTipSource.RESEARCHED
    ),
    CompassTip(
        category = "Strände",
        title = "Tigaki Beach direkt vor Ort",
        description = "Ein langer, flach abfallender Sandstrand direkt am Urlaubsort – gut für Familien geeignet.",
        note = "Liegen und Schirme saisonabhängig verfügbar",
        location = "Tigaki",
        journey = "zu Fuß je nach Hotel",
        tags = listOf("Strand", "Tigaki", "Familienfreundlich"),
        source = CompassTipSource.RESEARCHED
    ),
    CompassTip(
        category = "Strände",
        title = "Marmari Beach",
        description = "Ein ruhigerer Nachbarstrand von Tigaki, ebenfalls mit flachem Wasser.",
        note = "Für einen Ortswechsel ohne weite Fahrt",
        location = "Marmari, bei Tigaki",
        journey = "kurze Fahrt mit Auto oder Taxi",
        tags = listOf("Strand", "ruhig"),
        source = CompassTipSource.RESEARCHED
    ),
    CompassTip(
        category = "Tigaki",
        title = "Alikes Salzsee – Spaziergang & Vögel",
        description = "Ein Naturschutzgebiet mit Salzsee direkt neben Tigaki, besonders im Frühjahr und Herbst gut für Vogelbeobachtung.",
        note = "Sonnenschutz mitnehmen, wenig Schatten auf dem Weg",
        location = "Tigaki",
        journey = "kurzer Spaziergang vom Ort",
        tags = listOf("Tigaki", "Natur", "Spaziergang", "ohne Auto möglich"),
        source = CompassTipSource.RESEARCHED
    ),
    CompassTip(
        category = "Mobilität",
        title = "Bus ab Tigaki nach Kos-Stadt (KTEL Kos)",
        description = "Linienbusse von KTEL Kos verbinden Tigaki mit Kos-Stadt in etwa 20–30 Minuten.",
        note = "Aktuelle Fahrzeiten und Preise vorab auf ktel-kos.gr oder bei der Rezeption prüfen – hier nicht live eingebunden",
        kind = CompassTipKind.CAUTION,
        location = "Tigaki",
        journey = "Bus (KTEL Kos)",
        tags = listOf("Bus", "Tigaki", "Fahrplan"),
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://www.ktel-kos.gr/"
    ),
    CompassTip(
        category = "Mobilität",
        title = "Rückfahrt zuerst planen",
        description = "Bei Zielen mit wenigen Direktverbindungen zuerst die letzte passende Rückfahrt sichern und danach den Tagesablauf planen.",
        note = "Besonders wichtig für Ausflüge ohne Mietwagen",
        location = "Ab Tigaki",
        journey = "Busausflüge",
        tags = listOf("Bus", "Rückfahrt", "ohne Auto"),
        source = CompassTipSource.RESEARCHED
    ),
    CompassTip(
        category = "Essen",
        title = "Aliada",
        description = "Griechische Taverne in Tigaki. Gäste loben große Portionen, vegetarische Moussaka und gemischten Grillteller. Etwa 20–25 € pro Person laut Google.",
        note = "Öffnungszeiten vorab im Maps-Link prüfen, Telefon +30 2242 302662",
        location = "Tigaki",
        journey = "zu Fuß oder kurze Fahrt",
        tags = listOf("Essen", "Taverne", "Abendessen"),
        reviewSummary = "Google 4,8 von 5 (921 Bewertungen)",
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://wanderlog.com/list/geoCategory/370664/best-restaurants-to-have-dinner-in-tigaki"
    ),
    CompassTip(
        category = "Essen",
        title = "Brusko Restaurant",
        description = "Traditionelle griechische Küche mit Fokus auf Gegrilltes. Genannt werden Stifado, Lamm Kleftiko und Tzatziki.",
        note = "Griechische Abende mit Tanz laut e-restaurants.gr, dann unbedingt reservieren",
        location = "Tigaki",
        journey = "zu Fuß oder kurze Fahrt",
        tags = listOf("Essen", "Taverne", "Grill"),
        reviewSummary = "Google 4,7 von 5 (220 Bewertungen), Stand Juli 2026",
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://wanderlog.com/list/geoCategory/370664/best-restaurants-to-have-dinner-in-tigaki"
    ),
    CompassTip(
        category = "Essen",
        title = "Taverna Nisiotiko",
        description = "Authentische Taverne mit blau-weiß karierten Tischdecken. Beliebt sind Mixed Grill, Tzatziki und Halloumi.",
        note = "Gern reservieren, wenn es abends voll wird",
        location = "Tigaki",
        journey = "zu Fuß oder kurze Fahrt",
        tags = listOf("Essen", "Taverne", "Abendessen"),
        reviewSummary = "Google 4,6 von 5 (937 Bewertungen), Stand Juli 2026",
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://wanderlog.com/list/geoCategory/370664/best-restaurants-to-have-dinner-in-tigaki"
    ),
    CompassTip(
        category = "Essen",
        title = "Taverna Tsambala",
        description = "Griechische Taverne bei Kos-Stadt mit Speisen vor Ort und zum Mitnehmen. Etwa 15–40 € pro Person laut Google.",
        note = "Tisch online reservierbar über tavernatsambala.com. Anfahrt ab Tigaki einplanen",
        location = "Raum Kos-Stadt",
        journey = "Bus oder Taxi",
        tags = listOf("Essen", "Taverne", "Abendessen"),
        reviewSummary = "Google 4,6 von 5 (467 Bewertungen)",
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://tavernatsambala.com"
    ),
    CompassTip(
        category = "Essen",
        title = "Alikes",
        description = "Griechisches Restaurant in Tigaki mit Speisen vor Ort und zum Mitnehmen. Etwa 15–20 € pro Person laut Google.",
        note = "Abends bis 23:00 geöffnet, Öffnungszeiten vorab im Maps-Link prüfen",
        location = "Tigaki",
        journey = "zu Fuß oder kurze Fahrt",
        tags = listOf("Essen", "Taverne", "Abendessen"),
        reviewSummary = "Google 4,6 von 5 (1.433 Bewertungen)",
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://www.google.com/maps/place/Alikes/@36.8896838,27.1761253,17z"
    ),
    CompassTip(
        category = "Essen",
        title = "IKAROS Restaurant",
        description = "Familiengeführtes griechisches Restaurant in Tigaki mit Moussaka und Grillgerichten. Liegt an der Hauptstraße, abends kann es dort lauter sein.",
        note = "Bis Mitternacht geöffnet, Telefon +30 2242 068650",
        location = "Tigaki",
        journey = "zu Fuß oder kurze Fahrt",
        tags = listOf("Essen", "Taverne", "Abendessen"),
        reviewSummary = "Google 4,4 von 5 (954 Bewertungen)",
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://www.google.com/maps/place/IKAROS+Restaurant/@36.8860218,27.1852083,17z"
    ),
    CompassTip(
        category = "Essen",
        title = "Taverne Plóri",
        description = "Meeresfrüchte-Taverne direkt am Strand an der Straße vom Kreisel Richtung Salzsee. Der Wirt kocht laut kosurlaub.de täglich frische Suppen. Etwa 20–40 € pro Person laut Google.",
        note = "Bei Google im Oktober 2026 als vorübergehend geschlossen markiert, vermutlich Saisonpause. Vor dem Besuch Öffnung prüfen, Telefon +30 2242 069686",
        location = "Tigaki, Strand",
        journey = "zu Fuß",
        tags = listOf("Essen", "Taverne", "Fisch"),
        reviewSummary = "Google 4,8 von 5 (1.158 Bewertungen)",
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://www.kosurlaub.de/tigaki/"
    )
).let(::attachCompassTipIds)
