package com.example.kosurlaub.ui.home
import androidx.compose.ui.text.TextStyle
import com.example.kosurlaub.ui.theme.PaperPanel
import com.example.kosurlaub.ui.theme.PostcardChip
import com.example.kosurlaub.ui.theme.PostcardSpace
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
fun DiaryScreen(padding: PaddingValues, scrollToTopRequest: Int, phase: TripPhase, onOpenTips: () -> Unit) {
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
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .testTag("diary-screen"),
        verticalArrangement = Arrangement.spacedBy(PostcardSpace.Gap)
    ) {
        Text("Unser Tagebuch", style = PostcardType.Title, color = KosInk)
        Text(
            when (phase) {
                TripPhase.BEFORE -> "Sammelt, was ihr auf Kos erleben wollt. Unterwegs wächst hier euer Reisetagebuch."
                TripPhase.ON_KOS -> "Haltet fest, was ihr erlebt: Orte, Notizen und Fotos."
                TripPhase.AFTER -> "Eure Erinnerungen an Kos."
            },
            style = PostcardType.Quote, color = KosInk
        )

        val wishes = compassTips.filter { it.id in saved && it.id !in visited }
        val done = compassTips.filter { it.id in visited }

        if (wishes.isEmpty() && done.isEmpty()) {
            DiaryCard("Eure Tipps") {
                DiaryLine("Noch nichts vorgemerkt. Merkt euch Orte unter „Tipps“, hier hakt ihr sie später ab.")
                Spacer(Modifier.height(12.dp))
                PostcardChip("Tipps ansehen", true, onOpenTips, Modifier.testTag("diary-open-tips"))
            }
        }

        if (wishes.isNotEmpty()) {
            DiaryCard("Wollen wir noch") {
                wishes.forEach { tip ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        DiaryCheckbox(
                            checked = false,
                            onChange = { visited = CompassPreferences.toggleVisited(context, tip.id) },
                            modifier = Modifier.testTag("diary-wish-${tip.id}")
                        )
                        DiaryLine(tip.title)
                    }
                }
            }
        }

        if (done.isNotEmpty()) {
            DiaryCard("Erlebt") {
                done.forEach { tip ->
                    val date = CompassPreferences.visitedOn(context, tip.id)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        DiaryCheckbox(checked = true, onChange = { visited = CompassPreferences.toggleVisited(context, tip.id) })
                        DiaryLine(tip.title, modifier = Modifier.weight(1f))
                        if (date != null) {
                            DiaryLine(dateFormat.format(Date(date)), style = PostcardType.Label, alpha = 0.6f)
                        }
                    }
                }
            }
        }

        if (entriesByDay.isNotEmpty()) {
            DiaryCard("Einträge je Tag") {
                entriesByDay.forEach { (index, entries) ->
                    val day = TripDays.all.first { it.index == index }
                    Text(
                        "Tag $index · ${day.weekday} ${day.day}.${day.month}.",
                        style = PostcardType.Heading, color = KosInk,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    entries.forEach { entry ->
                        when (entry.kind) {
                            DayEntry.Kind.NOTE -> DiaryLine(entry.value)
                            DayEntry.Kind.PLACE -> DiaryLine("Ort: " + (compassTips.firstOrNull { it.id == entry.value }?.title ?: entry.value))
                            DayEntry.Kind.PHOTO -> DayPhoto(File(File(context.filesDir, "day_photos"), entry.value))
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

        // Vier Nullen vor dem Urlaub sagen nichts; der Rückblick erscheint, sobald etwas da ist.
        val stats = diaryStats(done.size, entriesByDay)
        if (phase == TripPhase.AFTER || stats.places + stats.notes + stats.photos + stats.daysWithEntries > 0) {
            DiaryCard(if (phase == TripPhase.AFTER) "Rückblick · Unser Kos" else "Rückblick · Bisher") {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Stat(stats.places, "Orte", Modifier.weight(1f))
                    Stat(stats.notes, "Notizen", Modifier.weight(1f))
                    Stat(stats.photos, "Fotos", Modifier.weight(1f))
                    Stat(stats.daysWithEntries, "Tage", Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun Stat(value: Int, label: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(value.toString(), style = PostcardType.Title, color = KosInk)
        PostcardLabel(label)
    }
}

@Composable
private fun DiaryCheckbox(checked: Boolean, onChange: () -> Unit, modifier: Modifier = Modifier) {
    Checkbox(
        checked = checked,
        onCheckedChange = { onChange() },
        colors = CheckboxDefaults.colors(
            checkedColor = KosInk,
            checkmarkColor = KosBg,
            uncheckedColor = KosInk.copy(alpha = 0.5f)
        ),
        modifier = modifier
    )
}

@Composable
private fun DiaryLine(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = PostcardType.Body,
    alpha: Float = 0.95f
) {
    Text(text, style = style, color = KosInk.copy(alpha = alpha), modifier = modifier)
}

@Composable
private fun DiaryCard(label: String, content: @Composable () -> Unit) {
    PaperPanel {
        PostcardLabel(label)
        Spacer(Modifier.height(8.dp))
        content()
    }
}
