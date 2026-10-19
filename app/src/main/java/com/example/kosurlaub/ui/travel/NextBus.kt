package com.example.kosurlaub.ui.travel

import java.util.Calendar

internal data class NextBus(val time: String, val minutesUntil: Int, val route: BusRoute)

/**
 * Nächste Abfahrt von [from] nach [to] ab [now] (Ortszeit Kos), oder null, wenn heute keine mehr fährt
 * oder die Strecke fehlt. Sonntags gilt die Sonntags-Tabelle, sonst "Mo–Sa".
 */
internal fun nextBus(
    from: String,
    to: String,
    now: Calendar,
    routes: List<BusRoute> = KtelKosTimetable.routes
): NextBus? {
    val prefix = if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) "Sonntag" else "Mo–Sa"
    val route = routes.firstOrNull { it.from == from && it.to == to && it.validity.startsWith(prefix) } ?: return null
    val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
    val time = route.departures.map { it.time }.firstOrNull { minutesOf(it) >= nowMinutes } ?: return null
    return NextBus(time, minutesOf(time) - nowMinutes, route)
}

private fun minutesOf(time: String): Int =
    time.substringBefore(":").toInt() * 60 + time.substringAfter(":").toInt()
