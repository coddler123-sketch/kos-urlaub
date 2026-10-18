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

import android.content.Context

object CompassPreferences {
    private const val PREFS = "kos_compass"
    private const val VISITED_ON_PREFIX = "visited_on_"
    private const val KEY_SAVED = "saved_tips"
    private const val KEY_VISITED = "visited_tips"
    private const val NOTE_PREFIX = "note_"
    private const val KEY_STABLE_IDS_MIGRATED = "stable_ids_migrated"

    fun migrateLegacyTitles(context: Context, tips: List<CompassTip>) {
        val preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (preferences.getBoolean(KEY_STABLE_IDS_MIGRATED, false)) return

        val titleToId = tips.associate { it.title to it.id }
        val editor = preferences.edit()
            .putStringSet(KEY_SAVED, migrateCompassSelection(stringSet(context, KEY_SAVED), titleToId))
            .putStringSet(KEY_VISITED, migrateCompassSelection(stringSet(context, KEY_VISITED), titleToId))

        tips.forEach { tip ->
            val legacyKey = NOTE_PREFIX + tip.title
            val stableKey = NOTE_PREFIX + tip.id
            if (preferences.contains(legacyKey) && !preferences.contains(stableKey)) {
                editor.putString(stableKey, preferences.getString(legacyKey, ""))
            }
            editor.remove(legacyKey)
        }
        editor.putBoolean(KEY_STABLE_IDS_MIGRATED, true).apply()
    }

    fun saved(context: Context): Set<String> = stringSet(context, KEY_SAVED)

    fun visited(context: Context): Set<String> = stringSet(context, KEY_VISITED)

    fun toggleSaved(context: Context, id: String): Set<String> =
        toggleAndSave(context, KEY_SAVED, id)

    fun toggleVisited(context: Context, id: String): Set<String> {
        val updated = toggleAndSave(context, KEY_VISITED, id)
        val editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        if (id in updated) editor.putLong(VISITED_ON_PREFIX + id, System.currentTimeMillis())
        else editor.remove(VISITED_ON_PREFIX + id)
        editor.apply()
        return updated
    }

    /** Zeitpunkt des Abhakens, null bei älteren Einträgen ohne Datum. */
    fun visitedOn(context: Context, id: String): Long? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong(VISITED_ON_PREFIX + id, 0L).takeIf { it > 0 }

    fun note(context: Context, id: String): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(NOTE_PREFIX + id, "")
            .orEmpty()

    fun saveNote(context: Context, id: String, note: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(NOTE_PREFIX + id, note)
            .apply()
    }

    private fun stringSet(context: Context, key: String): Set<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(key, emptySet())
            .orEmpty()
            .toSet()

    private fun toggleAndSave(context: Context, key: String, id: String): Set<String> {
        val updated = toggledSelection(stringSet(context, key), id)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(key, updated)
            .apply()
        return updated
    }
}

internal fun migrateCompassSelection(
    values: Set<String>,
    titleToId: Map<String, String>
): Set<String> = values.mapTo(mutableSetOf()) { titleToId[it] ?: it }
