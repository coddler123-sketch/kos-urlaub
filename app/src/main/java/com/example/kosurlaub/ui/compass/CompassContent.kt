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
        sourceUrl = "https://www.kosurlaub.de/tigaki/"    ),
    CompassTip(
        category = "Strände",
        title = "Agios Stefanos und Kastri",
        description = "Strand bei Kefalos im Südwesten mit den Ruinen einer frühchristlichen Basilika direkt am Wasser. Gegenüber liegt die kleine Felseninsel Kastri mit einer weißen Kapelle des heiligen Nikolaus.",
        note = "Sand mit kleinen Kieseln, Wasserschuhe helfen. Gegen späten Nachmittag lässt der Wind oft nach. Von Tigaki gut 40 km (Schätzung), Busverbindung vorab prüfen",
        location = "Agios Stefanos, Kefalos",
        journey = "Mietwagen oder Taxi, gut 40 km",
        tags = listOf("Strand", "Kultur", "Aussicht", "Halbtag"),
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://greeka.com/dodecanese/kos/beaches/agios-stefanos"
    ),
    CompassTip(
        category = "Strände",
        title = "Paradise Beach",
        description = "Langer heller Sandstrand an der Südküste bei Kefalos mit flach abfallendem Wasser, Strandbars und Tavernen. Beliebt und im Sommer oft voll.",
        note = "Liegen kosten extra (ein Bericht nennt 20 € für zwei Liegen mit Schirm, Preise vor Ort klären). Busse fahren selten, Fahrplan vorab prüfen; viele kommen per Mietwagen oder Tagestour",
        location = "Paradise Beach, Kefalos",
        journey = "Mietwagen, Taxi oder Tour, gut 40 km",
        tags = listOf("Strand", "Baden", "Halbtag"),
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://greeka.com/dodecanese/kos/beaches/paradise"
    ),
    CompassTip(
        category = "Ausflüge",
        title = "Palio Pyli – Geisterdorf und Burgruine",
        description = "Verlassene mittelalterliche Siedlung am Westhang des Inselgebirges mit einer Burgruine oberhalb. Der Aufstieg dauert je nach Bericht 20 bis 30 Minuten über steile Stufen.",
        note = "Ruine ungesichert, feste Schuhe nötig. Laut einer Burgenseite jederzeit frei zugänglich ohne Eintritt. Parken an einer kleinen Kirche oder an der Quelle Richtung Pyli. Gut mit Zia zu verbinden",
        kind = CompassTipKind.CAUTION,
        location = "Palio Pyli, bei Zia",
        journey = "Mietwagen oder Taxi",
        tags = listOf("Geschichte", "Aussicht", "Natur", "Halbtag"),
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://www.burgenwelt.org/griechenland/paliopyli/burgenwelt-paliopyli-objektseite.pdf"    ),
    CompassTip(
        category = "Tigaki",
        title = "Tigaki auf einen Blick",
        description = "Tigaki liegt gut 10 km westlich von Kos-Stadt. Vom Ortszentrum sind es etwa 150 m zum Meer und rund 400 m zum Alikes-Salzsee. Der lange Sandstrand zieht sich etwa 10 km am Ort entlang, gesäumt von Bars und Tavernen.",
        note = "Entfernungen stammen aus Reiseportalen und sind Richtwerte. Flughafen etwa 12 bis 13 km, Kos-Stadt mit Bus oder Taxi grob 15 bis 30 Minuten",
        location = "Tigaki",
        journey = "zu Fuß",
        tags = listOf("Tigaki", "Orientierung", "zu Fuß"),
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://www.discoveringkos.com/destination-item/tigkaki-or-tigaki/"
    ),
    CompassTip(
        category = "Tigaki",
        title = "Mit dem Rad an der Küste",
        description = "Die Gegend um Tigaki ist flach, deshalb lässt sie sich gut mit dem Fahrrad erkunden, etwa zum Salzsee oder an der Küste entlang. Nach Kos-Stadt braucht man mit dem Rad länger als mit dem Bus.",
        note = "Leihmöglichkeiten und Preise im Ort erfragen. Mittags Sonne und Wind einplanen, Wasser mitnehmen",
        location = "Tigaki",
        journey = "Fahrrad, vom Ort aus",
        tags = listOf("Tigaki", "Rad", "Natur", "ohne Auto möglich"),
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://www.onthebeach.co.uk/destinations/greece/kos/tigaki"
    ),
    CompassTip(
        category = "Tigaki",
        title = "Wassersport am Tigaki-Strand",
        description = "Am Strand sind Windsurfen, Stand-up-Paddling und Schnorcheln möglich, in der Gegend auch Kitesurfen. Das flache Wasser macht den Strand für Anfänger und Familien angenehm.",
        note = "Angebote und Preise der Stationen vor Ort prüfen. Bei Nordwind wird es wellig, im September ist der Meltemi meist schwächer",
        location = "Tigaki",
        journey = "zu Fuß je nach Hotel",
        tags = listOf("Tigaki", "Strand", "Aktiv"),
        source = CompassTipSource.RESEARCHED,
        sourceUrl = "https://www.hotelplan.ch/ferien/tigaki-kos-griechenland/f-40488"
    )
).let(::attachCompassTipIds)
