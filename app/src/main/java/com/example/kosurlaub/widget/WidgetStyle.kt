package com.example.kosurlaub.widget

import android.content.Context
import com.example.kosurlaub.R

/** Optik des 4x2-Widgets; der Stil wird pro platziertem Widget gespeichert. */
enum class WidgetStyle(
    val key: String,
    val layout: Int,
    val nameRes: Int,
    val descriptionRes: Int
) {
    ISLAND("island", R.layout.kos_countdown_widget_large, R.string.widget_style_island, R.string.widget_style_island_desc),
    BOARD("board", R.layout.kos_widget_board, R.string.widget_style_board, R.string.widget_style_board_desc),
    GLASS("glass", R.layout.kos_widget_glass, R.string.widget_style_glass, R.string.widget_style_glass_desc),
    TILE("tile", R.layout.kos_widget_tile, R.string.widget_style_tile, R.string.widget_style_tile_desc),
    COMPACT("compact", R.layout.kos_widget_compact, R.string.widget_style_compact, R.string.widget_style_compact_desc);

    companion object {
        private const val PREFS = "kos_widget_style"

        private fun prefs(context: Context) =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        /** Gespeicherter Stil, ohne Eintrag der bisherige Insel-Fokus. */
        fun of(context: Context, appWidgetId: Int): WidgetStyle {
            val key = prefs(context).getString("id_$appWidgetId", null)
            return values().firstOrNull { it.key == key } ?: ISLAND
        }

        fun save(context: Context, appWidgetId: Int, style: WidgetStyle) {
            prefs(context).edit().putString("id_$appWidgetId", style.key).apply()
        }

        fun forget(context: Context, appWidgetIds: IntArray) {
            prefs(context).edit().apply { appWidgetIds.forEach { remove("id_$it") } }.apply()
        }
    }
}
