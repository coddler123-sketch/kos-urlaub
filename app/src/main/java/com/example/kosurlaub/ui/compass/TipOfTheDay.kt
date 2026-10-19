package com.example.kosurlaub.ui.compass

import com.example.kosurlaub.ui.weather.KosWeather

internal data class TipOfTheDay(val tip: CompassTip, val reason: String)

private const val WINDY_KMH = 30
private const val HOT_CELSIUS = 28
private const val RAIN_MM = 0.5
private val rotation = listOf("Ausflüge", "Strände", "Essen")

/**
 * Ein Tipp pro Tag, nach Wetter gewählt: Wind → Ausflug, Hitze ohne Regen → Strand, Regen → Essen,
 * sonst Kategorien im Wechsel. Nur Empfehlungen aus Ausflügen, Stränden und Essen; stabil je [epochDay].
 */
internal fun tipOfTheDay(tips: List<CompassTip>, weather: KosWeather?, epochDay: Long): TipOfTheDay? {
    val (category, reason) = when {
        weather != null && weather.precipitationMm >= RAIN_MM -> "Essen" to "Regenpause: Zeit für eine Taverne."
        weather != null && weather.windSpeedKmh >= WINDY_KMH -> "Ausflüge" to "Windtag: passt zu einem Ausflug."
        weather != null && weather.temperatureCelsius >= HOT_CELSIUS -> "Strände" to "Heißer, trockener Tag: ab ans Wasser."
        else -> rotation[(epochDay % rotation.size).toInt()] to "Heute vorgeschlagen."
    }
    val candidates = tips.filter { it.kind == CompassTipKind.RECOMMENDATION && it.category == category }
        .ifEmpty { tips.filter { it.kind == CompassTipKind.RECOMMENDATION && it.category in rotation } }
    if (candidates.isEmpty()) return null
    return TipOfTheDay(candidates[(epochDay % candidates.size).toInt()], reason)
}
