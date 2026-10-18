package com.example.kosurlaub.ui.travel

import com.example.kosurlaub.R

internal data class PhotoSpot(
    val id: String,
    val title: String,
    val location: String,
    val bestTime: String,
    val timeCategory: String, // "Sonnenaufgang", "Vormittag", "Golden Hour", "Blaue Stunde", "Mittagslicht"
    val tip: String,
    val mapQuery: String,
    val iconEmoji: String
)

internal val photoSpots = listOf(
    PhotoSpot(
        id = "tigaki_beach",
        title = "Tigaki Beach am Morgen",
        location = "Tigaki",
        bestTime = "Frühmorgens (ca. 07:00–08:30 Uhr)",
        timeCategory = "Sonnenaufgang",
        tip = "Flaches Wasser und feiner Sand: Am frühen Morgen liegt das Meer oft spiegelglatt, die Berge der Türkei zeichnen sich im Dunst ab.",
        mapQuery = "Tigaki Beach, Kos",
        iconEmoji = "🌅"
    ),
    PhotoSpot(
        id = "alikes_flamingos",
        title = "Flamingos am Alikes-Salzsee",
        location = "Alikes, bei Tigaki",
        bestTime = "Früher Morgen oder später Nachmittag",
        timeCategory = "Natur",
        tip = "Kurzer Spaziergang von Tigaki. Mit Teleobjektiv oder Handy-Zoom Abstand halten, die Vögel sind scheu.",
        mapQuery = "Alikes Salt Lake, Tigaki, Kos",
        iconEmoji = "🦩"
    ),
    PhotoSpot(
        id = "neratzia_castle",
        title = "Johanniterburg Neratzia",
        location = "Kos-Stadt, am Hafen",
        bestTime = "Vormittag oder später Nachmittag",
        timeCategory = "Vormittag",
        tip = "Von der Brücke am Eingang und vom Hafenkai aus wirken die Mauern am besten. Mittags ist das Licht sehr hart.",
        mapQuery = "Castle of Neratzia, Kos",
        iconEmoji = "🏰"
    ),
    PhotoSpot(
        id = "hippocrates_plane",
        title = "Platane des Hippokrates",
        location = "Kos-Stadt, Platia Platanou",
        bestTime = "Vormittag",
        timeCategory = "Vormittag",
        tip = "Der alte Baum steht gestützt im Schatten; Detailfotos der Stützen und des Brunnens daneben funktionieren gut.",
        mapQuery = "Hippocrates Plane Tree, Kos",
        iconEmoji = "🌳"
    ),
    PhotoSpot(
        id = "asklepieion",
        title = "Asklepieion mit Meerblick",
        location = "bei Kos-Stadt",
        bestTime = "Vormittag (nach Öffnung, wenig Besucher)",
        timeCategory = "Vormittag",
        tip = "Von den oberen Terrassen reicht der Blick über die Küste bis zur Türkei. Die Säulen der obersten Ebene sind ein gutes Motiv.",
        mapQuery = "Asklepieion of Kos",
        iconEmoji = "🏛️"
    ),
    PhotoSpot(
        id = "zia_sunset",
        title = "Zia bei Sonnenuntergang",
        location = "Zia, Berge über Kos",
        bestTime = "Sonnenuntergang (Mitte September ca. 19:30–20:00 Uhr)",
        timeCategory = "Golden Hour",
        tip = "Gut eine halbe Stunde vor Sonnenuntergang da sein, damit ihr einen Platz mit freiem Blick habt. Rückfahrt vorher klären.",
        mapQuery = "Zia Village, Kos",
        iconEmoji = "🌇"
    ),
    PhotoSpot(
        id = "kastri_kefalos",
        title = "Kastri-Insel bei Kefalos",
        location = "Kefalos, Südwesten",
        bestTime = "Später Nachmittag",
        timeCategory = "Golden Hour",
        tip = "Die kleine Insel mit der weißen Kapelle liegt direkt vor dem Strand. Bei Niedrigwasser sieht man den Sandweg dorthin.",
        mapQuery = "Kastri Island, Kefalos, Kos",
        iconEmoji = "⛪"
    ),
    PhotoSpot(
        id = "paradise_beach",
        title = "Paradise Beach und Dünen",
        location = "Kefalos-Bucht, Südküste",
        bestTime = "Vormittag",
        timeCategory = "Vormittag",
        tip = "Weite Sandstrände und Dünen. Am besten von oben fotografieren, dort zeigt sich die Farbe des Wassers.",
        mapQuery = "Paradise Beach, Kos",
        iconEmoji = "🏖️"
    )
)
