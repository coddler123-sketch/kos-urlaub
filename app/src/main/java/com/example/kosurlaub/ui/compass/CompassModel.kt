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

data class CompassTip(
    val category: String,
    val title: String,
    val description: String,
    val note: String,
    val kind: CompassTipKind = CompassTipKind.RECOMMENDATION,
    val location: String = "",
    val journey: String = "",
    val tags: List<String> = emptyList(),
    val reviewSummary: String? = null,
    val hotelDetails: List<CompassTipDetail> = emptyList(),
    val source: CompassTipSource = CompassTipSource.COMMUNITY,
    val sourceUrl: String? = null,
    val mapsUrl: String? = null,
    val id: String = ""
)

data class CompassTipDetail(
    val title: String,
    val text: String
)

enum class CompassTipSource(val label: String) {
    COMMUNITY("COMMUNITY"),
    RESEARCHED("RECHERCHIERT")
}

enum class CompassTipKind(val label: String) {
    RECOMMENDATION("EMPFOHLEN"),
    NOTE("HINWEIS"),
    CAUTION("BEACHTEN")
}

internal val compassTipIds = listOf(
    "hotel-ilios-k-village",
    "trip-kos-town",
    "trip-asklepieion",
    "trip-zia-sunset",
    "trip-antimachia-castle",
    "trip-embros-therme",
    "trip-nisyros-boat",
    "trip-bodrum-boat",
    "beach-tigaki",
    "beach-marmari",
    "tigaki-alikes-salt-lake",
    "mobility-bus-tigaki-kos-town",
    "mobility-plan-return-first",
    "food-greek-classics",
    "food-aliada",
    "food-brusko",
    "food-nisiotiko",
    "food-tsambala",
    "food-alikes",
    "food-ikaros",
    "food-plori"
)

internal fun attachCompassTipIds(tips: List<CompassTip>): List<CompassTip> {
    require(tips.size == compassTipIds.size) { "Every compass tip needs a stable ID" }
    require(generatedCompassTipIds == compassTipIds.toSet()) {
        "Google-Maps-Links.csv must contain every compass tip ID exactly once"
    }
    return tips.mapIndexed { index, tip ->
        val id = compassTipIds[index]
        tip.copy(id = id, mapsUrl = generatedCompassTipMapsUrls[id])
    }
}

internal fun filterCompassTips(
    tips: List<CompassTip>,
    query: String,
    category: String? = null
): List<CompassTip> {
    val normalizedQuery = query.trim().lowercase()
    return tips.filter { tip ->
        val matchesCategory = category == null || tip.category == category
        val searchable = listOf(
            tip.category,
            tip.title,
            tip.description,
            tip.note,
            tip.location,
            tip.journey,
            tip.reviewSummary.orEmpty(),
            tip.hotelDetails.joinToString(" ") { "${it.title} ${it.text}" },
            tip.tags.joinToString(" ")
        ).joinToString(" ").lowercase()
        matchesCategory && (normalizedQuery.isEmpty() || normalizedQuery in searchable)
    }
}

internal fun matchesCompassQuickFilter(tip: CompassTip, filter: String): Boolean = when (filter) {
    "📍 Tigaki" -> tip.location.contains("Tigaki", ignoreCase = true) ||
        tip.tags.any { it.contains("Tigaki", ignoreCase = true) }
    "🏛️ Kos-Stadt" -> tip.location.contains("Kos-Stadt", ignoreCase = true) ||
        tip.location.contains("Asklepieion", ignoreCase = true)
    "🏰 Süden & Inland" -> tip.location.contains("Zia", ignoreCase = true) ||
        tip.location.contains("Antimachia", ignoreCase = true) ||
        tip.location.contains("Kefalos", ignoreCase = true) ||
        tip.location.contains("Agios Fokas", ignoreCase = true)
    "🚤 Bootsausflüge" -> tip.location.contains("Nisyros", ignoreCase = true) ||
        tip.location.contains("Bodrum", ignoreCase = true)
    "Rund ums Hotel" -> tip.location.contains("Tigaki", ignoreCase = true) ||
        tip.tags.any { it.contains("Tigaki", ignoreCase = true) } ||
        tip.journey.contains("zu Fuß", ignoreCase = true)
    "Zu Fuß" -> tip.journey.contains("zu Fuß", ignoreCase = true) ||
        tip.tags.any { it.contains("zu Fuß", ignoreCase = true) }
    "15–30 Min Bus/Taxi" -> tip.journey.contains("Bus", ignoreCase = true) ||
        tip.journey.contains("15 Min", ignoreCase = true) ||
        tip.journey.contains("20 Min", ignoreCase = true) ||
        tip.journey.contains("30 Min", ignoreCase = true)
    "Tagesausflug" -> tip.tags.any { it.contains("Halbtag", ignoreCase = true) } ||
        tip.tags.any { it.contains("Ausflug", ignoreCase = true) || it.contains("Tagesausflug", ignoreCase = true) }
    "Ohne Auto" -> tip.tags.any { it.contains("ohne Auto", ignoreCase = true) } ||
        tip.journey.contains("zu Fuß", ignoreCase = true) ||
        tip.journey.contains("Bus", ignoreCase = true)
    "Halber Tag" -> tip.tags.any { it.contains("Halbtag", ignoreCase = true) } ||
        editorialFor(tip).duration.contains("Halber", ignoreCase = true) ||
        editorialFor(tip).duration.contains("2–3", ignoreCase = true) ||
        editorialFor(tip).duration.contains("3–4", ignoreCase = true)
    "Ruhig" -> tip.tags.any { it.contains("ruhig", ignoreCase = true) } ||
        tip.description.contains("ruhig", ignoreCase = true) ||
        tip.note.contains("entspannt", ignoreCase = true)
    "Essen" -> tip.category == "Essen" || tip.tags.any { it.contains("Taverne", ignoreCase = true) }
    else -> true
}
