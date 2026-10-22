package com.example.kosurlaub.ui.travel
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import android.content.Context

object TravelPreferences {
    private const val PREFS = "kos_travel"
    private const val KEY_CHECKLIST = "completed_checklist_items"
    private const val KEY_NOTES = "travel_notes"

    fun completedChecklistItems(context: Context): Set<String> =
        stringSet(context, KEY_CHECKLIST)

    fun toggleChecklistItem(context: Context, id: String): Set<String> =
        toggleAndSave(context, KEY_CHECKLIST, id)

    fun notes(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_NOTES, "")
            .orEmpty()

    fun saveNotes(context: Context, notes: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_NOTES, notes)
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

fun toggledSelection(current: Set<String>, id: String): Set<String> =
    current.toMutableSet().apply {
        if (!add(id)) remove(id)
    }
