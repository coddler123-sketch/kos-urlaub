package com.example.kosurlaub.ui.lists

import android.content.Context

/** Ein Punkt einer Liste. [group] ist bei der Packliste die Tasche, bei Aufgaben leer. [hint] ist optional. */
data class ListEntry(val group: String, val text: String, val hint: String, val done: Boolean)

object ListStore {
    private const val PREFS = "kos_lists"
    const val PACKING = "packing"
    const val TASKS = "tasks"

    val bags = listOf("Dokumente", "Koffer", "Handgepäck", "Strandtasche")

    private fun p(group: String, text: String, hint: String = "") = ListEntry(group, text, hint, false)

    val packingSeed = listOf(
        p("Dokumente", "Personalausweis oder Reisepass"),
        p("Dokumente", "Europäische Krankenversicherungskarte"),
        p("Dokumente", "Flug- und Hotelbestätigung offline"),
        p("Dokumente", "Police der Reiseversicherung"),
        p("Dokumente", "Führerschein, falls ein Auto kommt"),
        p("Koffer", "Badesachen"),
        p("Koffer", "Sonnenhut"),
        p("Koffer", "Sonnencreme LSF 50"),
        p("Koffer", "Leichte lange Hose für den Abend"),
        p("Koffer", "Sandalen und geschlossene Schuhe"),
        p("Koffer", "Moskitomittel"),
        p("Koffer", "Waschzeug"),
        p("Handgepäck", "Medikamente"),
        p("Handgepäck", "Ladegerät und Powerbank"),
        p("Handgepäck", "Dünne Schicht für das Flugzeug"),
        p("Handgepäck", "Leere Flasche, nach der Kontrolle füllen"),
        p("Strandtasche", "Wasserschuhe"),
        p("Strandtasche", "Strandtuch"),
        p("Strandtasche", "Kleine wasserdichte Tasche für das Boot")
    )

    val tasksSeed = listOf(
        p("", "Ausweisgültigkeit prüfen", "Muss am Rückflug (28.09.) noch gelten."),
        p("", "Online-Check-in machen", "Öffnet bei Condor einige Stunden vor Abflug. Bordkarten speichern."),
        p("", "Versicherung entscheiden", "Die Karte der Krankenkasse holt niemanden nach Hause."),
        p("", "Transfer zum Hotel klären", "Tigaki liegt etwa 15 Minuten vom Flughafen. Beim Veranstalter nachsehen."),
        p("", "Bargeld einstecken", "Für Bus, Boot und kleine Kioske."),
        p("", "Sonnenschutz nachkaufen", "Nach dem Flug nicht darauf hoffen."),
        p("", "Handy: Roaming und Offline-Karten", "Karte von Kos vorher herunterladen.")
    )

    fun encode(items: List<ListEntry>): String =
        items.joinToString("\n") { listOf(if (it.done) "1" else "0", clean(it.group), clean(it.text), clean(it.hint)).joinToString("\t") }

    fun decode(raw: String): List<ListEntry> =
        raw.lines().mapNotNull { line ->
            val f = line.split("\t")
            if (f.size < 4 || f[2].isBlank()) null else ListEntry(f[1], f[2], f[3], f[0] == "1")
        }

    /** Gespeicherte Liste, beim ersten Start die Vorgabe. */
    fun load(context: Context, key: String): List<ListEntry> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(key, null)
        return if (raw == null) seed(key) else decode(raw)
    }

    fun save(context: Context, key: String, items: List<ListEntry>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(key, encode(items)).apply()
    }

    fun seed(key: String): List<ListEntry> = if (key == PACKING) packingSeed else tasksSeed

    fun progress(items: List<ListEntry>): Pair<Int, Int> = items.count { it.done } to items.size

    private fun clean(s: String) = s.replace('\t', ' ').replace('\n', ' ').trim()
}
