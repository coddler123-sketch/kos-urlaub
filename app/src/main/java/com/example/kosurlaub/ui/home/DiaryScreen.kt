package com.example.kosurlaub.ui.home
import com.example.kosurlaub.ui.theme.PostcardMetrics

import com.example.kosurlaub.ui.theme.PostcardLabel
import com.example.kosurlaub.ui.theme.PostcardType
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.ui.compass.CompassPreferences
import com.example.kosurlaub.ui.compass.compassTips
import com.example.kosurlaub.ui.theme.HomeAccent
import com.example.kosurlaub.ui.theme.HomeCardBorder
import com.example.kosurlaub.ui.theme.HomeCardColor
import com.example.kosurlaub.ui.theme.HomeCardShape
import com.example.kosurlaub.ui.theme.KosBg
import com.example.kosurlaub.ui.theme.KosInk
import com.example.kosurlaub.ui.theme.KosSans
import com.example.kosurlaub.ui.theme.KosSerif
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Zahlen für den Rückblick, rein aus den lokalen Einträgen berechnet. */
data class DiaryStats(val places: Int, val notes: Int, val photos: Int, val daysWithEntries: Int)

fun diaryStats(visitedCount: Int, entriesByDay: Map<Int, List<DayEntry>>): DiaryStats {
    val all = entriesByDay.values.flatten()
    return DiaryStats(
        places = visitedCount,
        notes = all.count { it.kind == DayEntry.Kind.NOTE },
        photos = all.count { it.kind == DayEntry.Kind.PHOTO },
        daysWithEntries = entriesByDay.count { it.value.isNotEmpty() }
    )
}

/** Tagebuch: Merkliste „Wollen wir noch“, Erlebtes, Einträge je Tag und Rückblick. */
@Composable
fun DiaryScreen(padding: PaddingValues, scrollToTopRequest: Int, phase: TripPhase) {
    val context = LocalContext.current
    val scroll = rememberScrollState()
    LaunchedEffect(scrollToTopRequest) { if (scrollToTopRequest > 0) scroll.animateScrollTo(0) }

    var saved by remember { mutableStateOf(CompassPreferences.saved(context)) }
    var visited by remember { mutableStateOf(CompassPreferences.visited(context)) }
    val entriesByDay = remember(visited) {
        TripDays.all.associate { it.index to DayEntryStore.load(context, it.index) }.filterValues { it.isNotEmpty() }
    }
    val dateFormat = remember { SimpleDateFormat("dd.MM.", Locale.GERMANY) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(scroll)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .testTag("diary-screen")
    ) {
        Text("Unser Tagebuch", color = KosInk, fontSize = 32.sp, fontWeight = FontWeight.Bold, fontFamily = KosSerif)
        Spacer(Modifier.height(16.dp))

        val wishes = compassTips.filter { it.id in saved && it.id !in visited }
        val done = compassTips.filter { it.id in visited }

        DiaryCard("WOLLEN WIR NOCH") {
            if (wishes.isEmpty()) {
                DiaryLine("Nichts vorgemerkt. Unter „Tipps“ könnt ihr Orte merken.", 0.7f)
            }
            wishes.forEach { tip ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = { visited = CompassPreferences.toggleVisited(context, tip.id) },
                        colors = CheckboxDefaults.colors(checkedColor = HomeAccent, uncheckedColor = KosInk.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("diary-wish-${tip.id}")
                    )
                    DiaryLine(tip.title)
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        DiaryCard("ERLEBT") {
            if (done.isEmpty()) DiaryLine("Noch nichts abgehakt.", 0.7f)
            done.forEach { tip ->
                val date = CompassPreferences.visitedOn(context, tip.id)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Checkbox(
                        checked = true,
                        onCheckedChange = { visited = CompassPreferences.toggleVisited(context, tip.id) },
                        colors = CheckboxDefaults.colors(checkedColor = HomeAccent, uncheckedColor = KosInk.copy(alpha = 0.5f))
                    )
                    DiaryLine(tip.title, modifier = Modifier.weight(1f))
                    if (date != null) DiaryLine(dateFormat.format(Date(date)), 0.6f, 12)
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        if (entriesByDay.isNotEmpty()) {
            DiaryCard("EINTRÄGE JE TAG") {
                entriesByDay.forEach { (index, entries) ->
                    val day = TripDays.all.first { it.index == index }
                    Text(
                        "Tag $index · ${day.weekday} ${day.day}.${day.month}.",
                        color = HomeAccent, fontSize = PostcardMetrics.BODY_SP.sp, fontFamily = KosSerif, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    entries.forEach { entry ->
                        when (entry.kind) {
                            DayEntry.Kind.NOTE -> DiaryLine(entry.value)
                            DayEntry.Kind.PLACE -> DiaryLine("📍 " + (compassTips.firstOrNull { it.id == entry.value }?.title ?: entry.value))
                            DayEntry.Kind.PHOTO -> DayPhoto(File(File(context.filesDir, "day_photos"), entry.value))
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        val stats = diaryStats(done.size, entriesByDay)
        DiaryCard(if (phase == TripPhase.AFTER) "RÜCKBLICK · UNSER KOS" else "RÜCKBLICK · BISHER") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Stat(stats.places, "Orte")
                Stat(stats.notes, "Notizen")
                Stat(stats.photos, "Fotos")
                Stat(stats.daysWithEntries, "Tage")
            }
        }
    }
}

@Composable
private fun Stat(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp)) {
        Text(value.toString(), color = KosInk, fontSize = 28.sp, fontFamily = KosSerif, fontWeight = FontWeight.Bold)
        Text(label, color = KosInk.copy(alpha = 0.7f), fontSize = PostcardMetrics.LABEL_SP.sp, fontFamily = KosSans)
    }
}

@Composable
private fun DiaryLine(text: String, alpha: Float = 0.95f, size: Int = 14, modifier: Modifier = Modifier) {
    Text(text, color = KosInk.copy(alpha = alpha), fontSize = size.sp, lineHeight = (size + 6).sp, fontFamily = KosSans, modifier = modifier)
}

@Composable
private fun DiaryCard(label: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeCardColor)
            .border(1.dp, HomeCardBorder, HomeCardShape)
            .padding(16.dp)
    ) {
        PostcardLabel(label)
        Spacer(Modifier.height(8.dp))
        content()
    }
}
