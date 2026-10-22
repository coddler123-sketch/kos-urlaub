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

import androidx.annotation.DrawableRes

data class CompassEditorial(
    val duration: String,
    val bestTime: String,
    val fromHotel: String,
    val transport: String,
    val septemberNote: String,
    val combination: String,
    val returnTip: String,
    val facilities: String,
    val counterRecommendation: String,
    val checkedAt: String = "15.10.2026",
    val validUntil: String? = null,
    @param:DrawableRes val imageRes: Int
)

private const val PLACEHOLDER_IMAGE = R.drawable.ic_sparkle

private val tipImages = mapOf(
    "trip-kos-town" to R.drawable.excursion_old_town,
    "trip-asklepieion" to R.drawable.excursion_kos_stadt,
    "trip-zia-sunset" to R.drawable.tip_zia_sunset,
    "trip-antimachia-castle" to R.drawable.tip_antimachia_castle,
    "trip-embros-therme" to R.drawable.tip_embros_therme,
    "trip-nisyros-boat" to R.drawable.kos_rot_11,
    "trip-bodrum-boat" to R.drawable.kos_rot_07,
    "beach-tigaki" to R.drawable.kos_rot_05,
    "beach-marmari" to R.drawable.excursion_marmari,
    "tigaki-alikes-salt-lake" to R.drawable.excursion_seven_springs,
    "mobility-bus-tigaki-kos-town" to R.drawable.kos_rot_19,
    "mobility-plan-return-first" to R.drawable.kos_rot_04,
    "food-aliada" to R.drawable.kos_rot_15,
    "food-brusko" to R.drawable.kos_rot_26,
    "food-nisiotiko" to R.drawable.kos_rot_12,
    "food-tsambala" to R.drawable.kos_rot_24,
    "food-alikes" to R.drawable.excursion_seven_springs,
    "food-ikaros" to R.drawable.category_greek_food,
    "food-plori" to R.drawable.category_greek_food,
    "beach-agios-stefanos" to R.drawable.tip_agios_stefanos,
    "beach-paradise" to R.drawable.tip_paradise_beach,
    "trip-palio-pyli" to R.drawable.tip_palio_pyli,
)

internal fun editorialFor(tip: CompassTip): CompassEditorial {
    val fallback = when (tip.category) {
        "Essen" -> CompassEditorial(
            "1–2 Stunden", "Abends", "Je nach Lokal", "Zu Fuß, Bus oder Taxi",
            "Öffnungstage und Reservierung kurz prüfen.",
            "Mit einem Spaziergang oder Strandabend verbinden.", "Rückweg vor dem Essen klären.",
            "Vom jeweiligen Lokal abhängig", "Nicht ideal, wenn ihr sehr knapp geplant seid.",
            imageRes = PLACEHOLDER_IMAGE
        )
        "Strände" -> CompassEditorial(
            "Halber Tag", "Vormittag", "Ab Tigaki unterschiedlich", "Zu Fuß, Taxi oder Auto",
            "Im September bleibt das Meer meist warm; Wind und Rückfahrt tagesaktuell prüfen.",
            "Mit einer nahen Taverne verbinden.", "Letzte Busverbindung vorab prüfen.",
            "Liegen und Gastronomie nicht überall garantiert", "Weniger passend bei starkem Wind.",
            imageRes = PLACEHOLDER_IMAGE
        )
        "Ausflüge" -> CompassEditorial(
            "Halber Tag", "Früh starten", "Ab Tigaki", "Bus, Taxi oder Auto",
            "Öffnungszeiten und Verbindungen vorab prüfen – manche Angaben in dieser App sind nicht live geprüft.",
            "Mit einem passenden Strand- oder Essensstopp verbinden.", "Rückfahrt zuerst festlegen.",
            "Wasser und Sonnenschutz mitnehmen", "Weniger passend für einen reinen Ruhetag.",
            imageRes = PLACEHOLDER_IMAGE
        )
        "Mobilität" -> CompassEditorial(
            "10 Minuten Planung", "Am Vorabend", "Ab Tigaki", "Je nach Ziel",
            "Fahrpläne vor der Reise erneut bei KTEL Kos abgleichen.",
            "Direkt beim jeweiligen Ausflug anwenden.", "Hin- und Rückfahrt gemeinsam prüfen.",
            "Ticket und etwas Zeitpuffer einplanen", "Spontanität ist bei seltenen Linien riskant.",
            imageRes = PLACEHOLDER_IMAGE
        )
        "Tigaki" -> CompassEditorial(
            "1–2 Stunden", "Vormittag oder später Nachmittag", "Direkt in Tigaki", "Zu Fuß",
            "Im September meist angenehm für einen ruhigen Spaziergang.",
            "Mit einem Strandtag oder Abendessen im Ort verbinden.", "Keine feste Rückfahrt nötig.",
            "Infrastruktur im Ferienort", "Weniger passend, wenn ihr bewusst Trubel sucht.",
            imageRes = PLACEHOLDER_IMAGE
        )
        else -> CompassEditorial(
            "Individuell", "Vor der Buchung", "Ab Tigaki", "Je nach Unterkunft",
            "Leistungen und Saisonbetrieb bestätigen lassen.",
            "Mit euren Tageszielen abgleichen.", "Transfer vorab klären.",
            "Details beim Anbieter prüfen", "Nur sinnvoll, wenn Lage und Angebot zu euch passen.",
            imageRes = PLACEHOLDER_IMAGE
        )
    }

    val editorial = when (tip.title) {
        "Bus ab Tigaki nach Kos-Stadt (KTEL Kos)" -> fallback.copy(
            duration = "Ca. 20–30 Min. Fahrtzeit nach Kos-Stadt",
            bestTime = "Vormittags für den Hinweg, später Nachmittag für den Rückweg",
            returnTip = "Aktuellen Fahrplan auf ktel-kos.gr prüfen – in dieser App nicht live eingebunden.",
            counterRecommendation = "Wer es flexibler mag, wählt Mietwagen oder Roller."
        )
        "Zia – Sonnenuntergang im Bergdorf" -> fallback.copy(
            duration = "2–3 Stunden",
            bestTime = "Später Nachmittag / Sonnenuntergang",
            counterRecommendation = "Öffentliche Busverbindung nicht sicher bestätigt – vorab klären."
        )
        else -> fallback
    }
    return tipImages[tip.id]?.let { editorial.copy(imageRes = it) } ?: editorial
}

internal fun featuredCompassTip(tips: List<CompassTip>, dayOfYear: Int): CompassTip =
    tips[Math.floorMod(dayOfYear - 1, tips.size)]

internal fun buildCompassDayPlan(tips: List<CompassTip>, savedIds: Set<String>): List<CompassTip> {
    val timeOrder = listOf("Sehr früh", "Zum Öffnungsbeginn", "Früh am Vormittag", "Vormittag", "Abends")
    return tips
        .filter { it.id in savedIds }
        .sortedBy { tip ->
            val bestTime = editorialFor(tip).bestTime
            timeOrder.indexOfFirst { bestTime.contains(it, ignoreCase = true) }
                .takeIf { it >= 0 } ?: timeOrder.size
        }
        .take(3)
}
