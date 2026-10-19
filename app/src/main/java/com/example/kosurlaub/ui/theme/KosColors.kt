package com.example.kosurlaub.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/** Semantische Farben der Reisetagebuch-Optik, je Hell/Dunkel eine Palette. */
@Immutable
class KosColors(
    val isDark: Boolean,
    val bg: Color,
    val nav: Color,
    val panel: Color,
    val panelHigh: Color,
    val card: Color,
    val cardBorder: Color,
    val ink: Color,
    val accent: Color,
    val accent2: Color,
    val onAccent: Color,
    val ok: Color,
    val warn: Color,
    val scrim: Color,
    val stamp: Color,
    val numeral: Color
)

val DarkKosColors = KosColors(
    isDark = true,
    bg = Color(0xFF1E1B18),
    nav = Color(0xFF26221E),
    panel = Color(0xFF2B2622),
    panelHigh = Color(0xFF38312B),
    card = Color(0xB32B2622),
    cardBorder = Color(0x33FFFFFF),
    ink = Color(0xFFF3ECDD),
    accent = Color(0xFF7FC4DA),
    accent2 = Color(0xFF9ED6E6),
    onAccent = Color(0xFF16313B),
    ok = Color(0xFF5ED6A2),
    warn = Color(0xFFFFB86B),
    scrim = Color.Black,
    stamp = Color(0xFFE0735F),
    numeral = Color(0xFF7FC4DA)
)

val LightKosColors = KosColors(
    isDark = false,
    bg = Color(0xFFF6F0E4),
    nav = Color(0xFFEFE6D0),
    panel = Color(0xFFF0E8D4),
    panelHigh = Color(0xFFE6DCC4),
    card = Color(0xE6FFFAF0),
    cardBorder = Color(0x332B3A30),
    ink = Color(0xFF2B3A30),
    accent = Color(0xFF1F5F7A),
    accent2 = Color(0xFF2A7896),
    onAccent = Color.White,
    ok = Color(0xFF2E8B62),
    warn = Color(0xFFB9731E),
    scrim = Color(0xFFF6F0E4),
    stamp = Color(0xFFB8432F),
    numeral = Color(0xFF1F5F7A)
)

val LocalKosColors = compositionLocalOf { DarkKosColors }
