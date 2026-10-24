package com.example.kosurlaub.ui.theme
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.widget.*

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val base = androidx.compose.material3.Typography()

private fun TextStyle.serif() = copy(fontFamily = KosSerif)
private fun TextStyle.sans() = copy(fontFamily = KosSans)

/** Material-Bauteile (Dialoge, Buttons, Navigationsleiste) sprechen dieselben Schriften wie der Rest: Fraunces für Überschriften, Inter sonst. */
val Typography = Typography(
    displayLarge = base.displayLarge.serif(),
    displayMedium = base.displayMedium.serif(),
    displaySmall = base.displaySmall.serif(),
    headlineLarge = base.headlineLarge.serif(),
    headlineMedium = base.headlineMedium.serif(),
    headlineSmall = base.headlineSmall.serif(),
    titleLarge = base.titleLarge.serif(),
    titleMedium = base.titleMedium.sans(),
    titleSmall = base.titleSmall.sans(),
    bodyLarge = TextStyle(
        fontFamily = KosSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = base.bodyMedium.sans(),
    bodySmall = base.bodySmall.sans(),
    labelLarge = base.labelLarge.sans(),
    labelMedium = base.labelMedium.sans(),
    labelSmall = base.labelSmall.sans()
)
