package com.example.kosurlaub.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.example.kosurlaub.R

// Variable Fonts: Gewichte ab Android 8 (API 26), darunter wird die Standardstärke genutzt.
@OptIn(ExperimentalTextApi::class)
private fun variable(res: Int, weight: FontWeight) =
    Font(res, weight, variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)))

/** Fließtext und Bedienelemente. */
val KosSans = FontFamily(
    variable(R.font.inter_variable, FontWeight.Normal),
    variable(R.font.inter_variable, FontWeight.Medium),
    variable(R.font.inter_variable, FontWeight.SemiBold),
    variable(R.font.inter_variable, FontWeight.Bold),
)

/** Überschriften und große Zahlen (Tagebuch-Optik). */
val KosSerif = FontFamily(
    variable(R.font.fraunces_variable, FontWeight.Normal),
    variable(R.font.fraunces_variable, FontWeight.SemiBold),
    variable(R.font.fraunces_variable, FontWeight.Bold),
)
