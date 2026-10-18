package com.example.kosurlaub.ui.home
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

data class CountdownMilestone(
    val title: String,
    val message: String
)

internal fun countdownMilestone(days: Long): CountdownMilestone? = when (days) {
    30L -> CountdownMilestone("NOCH 30 TAGE", "Ein Monat bis Kos")
    14L -> CountdownMilestone("NOCH 14 TAGE", "In zwei Wochen geht es los")
    7L -> CountdownMilestone("NOCH 7 TAGE", "Nur noch eine Woche")
    1L -> CountdownMilestone("MORGEN", "Morgen geht es nach Kos")
    else -> null
}
