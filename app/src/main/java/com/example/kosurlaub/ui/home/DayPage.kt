package com.example.kosurlaub.ui.home
import com.example.kosurlaub.ui.theme.PostcardMetrics

import com.example.kosurlaub.ui.theme.PostcardLabel
import com.example.kosurlaub.ui.theme.PostcardType
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.compass.CompassPreferences
import com.example.kosurlaub.ui.compass.compassTips
import com.example.kosurlaub.ui.theme.HomeAccent
import com.example.kosurlaub.ui.theme.HomeCardBorder
import com.example.kosurlaub.ui.theme.HomeCardColor
import com.example.kosurlaub.ui.theme.HomeCardShape
import com.example.kosurlaub.ui.theme.KosInk
import com.example.kosurlaub.ui.theme.KosSans
import com.example.kosurlaub.ui.theme.KosSerif
import com.example.kosurlaub.ui.travel.BusDeparture
import com.example.kosurlaub.ui.travel.KtelKosTimetable
import com.example.kosurlaub.ui.weather.WeatherRepository
import java.io.File

private val fullWeekdays = mapOf(
    "Mo" to "Montag", "Di" to "Dienstag", "Mi" to "Mittwoch", "Do" to "Donnerstag",
    "Fr" to "Freitag", "Sa" to "Samstag", "So" to "Sonntag"
)

/** Tagesseite: nur Fakten und Platz für Eigenes, keine Vorschläge. */
@Composable
fun DayPage(day: TripDay, onOpenTips: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().testTag("day-page")) {
        Text(
            "TAG ${day.index} VON ${TripDays.all.size}",
            color = HomeAccent, fontSize = PostcardMetrics.LABEL_SP.sp, fontWeight = FontWeight.Bold,
            fontFamily = KosSans, letterSpacing = 1.sp, modifier = Modifier.testTag("home-selected-day")
        )
        Text(
            "${fullWeekdays[day.weekday]}, %02d.%02d.".format(day.day, day.month),
            color = KosInk, fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = KosSerif
        )
        Spacer(Modifier.height(14.dp))
        FactsCard(day)
        Spacer(Modifier.height(12.dp))
        BusFactCard(day)
        Spacer(Modifier.height(12.dp))
        YourDayCard(day)
        Spacer(Modifier.height(12.dp))
        Text(
            "Tipps ansehen  ›",
            color = KosInk.copy(alpha = 0.75f), fontSize = PostcardMetrics.BODY_SP.sp, fontFamily = KosSerif,
            modifier = Modifier.clickable(onClick = onOpenTips).padding(vertical = 10.dp).testTag("day-ideas")
        )
    }
}

@Composable
private fun DayCard(label: String, content: @Composable () -> Unit) {
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

@Composable
private fun Line(text: String, alpha: Float = 0.95f, size: Int = 14) {
    Text(text, color = KosInk.copy(alpha = alpha), fontSize = size.sp, lineHeight = (size + 6).sp, fontFamily = KosSans)
}

@Composable
private fun FactsCard(day: TripDay) {
    val context = LocalContext.current
    val sun = remember(day) {
        SunTimes.compute(TripData.HOTEL_LAT, TripData.HOTEL_LON, day.year, day.month, day.day, "Europe/Athens")
    }
    val iso = "%04d-%02d-%02d".format(day.year, day.month, day.day)
    val forecast = remember(day) { WeatherRepository.cached(context)?.forecast?.firstOrNull { it.dateIso == iso } }
    DayCard("FAKTEN TIGAKI") {
        if (sun != null) Line("Sonne: Aufgang ${sun.sunrise} · Untergang ${sun.sunset} (berechnet)")
        if (forecast != null) {
            Line("Wetter: ${forecast.minTemperatureCelsius}–${forecast.maxTemperatureCelsius}° · Regen ${forecast.precipitationProbabilityPercent ?: 0} %")
        } else {
            Line("Wetter: Prognose erscheint wenige Tage vorher.", alpha = 0.7f)
        }
        if (day.index == 1) {
            Line("Hinflug ${TripData.outbound.number}: ${TripData.outbound.fromCity} ${"%02d:%02d".format(TripData.outbound.departHour, TripData.outbound.departMinute)} → Kos ${"%02d:%02d".format(TripData.outbound.arriveHour, TripData.outbound.arriveMinute)} (Ortszeit)")
        }
        if (day.index == TripDays.all.size) {
            Line("Rückflug ${TripData.inbound.number}: Kos ${"%02d:%02d".format(TripData.inbound.departHour, TripData.inbound.departMinute)} → ${TripData.inbound.toCity} ${"%02d:%02d".format(TripData.inbound.arriveHour, TripData.inbound.arriveMinute)} (Ortszeit)")
        }
    }
}

@Composable
private fun BusFactCard(day: TripDay) {
    val sunday = day.weekday == "So"
    val out = KtelKosTimetable.routes.firstOrNull { it.from == "Tigaki" && (it.validity.startsWith("Sonntag") == sunday) }
    val back = KtelKosTimetable.routes.firstOrNull { it.from == "Kos-Stadt" && (it.validity.startsWith("Sonntag") == sunday) }
    DayCard("BUS TIGAKI ↔ KOS-STADT") {
        if (out != null && back != null) {
            Line("Ab Tigaki: " + out.departures.times())
            Spacer(Modifier.height(6.dp))
            Line("Ab Kos-Stadt: " + back.departures.times())
            Spacer(Modifier.height(6.dp))
            Line("Referenz KTEL Kos, Stand ${out.asOf}. Fahrplan für September 2027 noch nicht veröffentlicht.", alpha = 0.6f, size = 11)
        } else {
            Line("Fahrplan nicht verifiziert, bitte bei KTEL Kos prüfen.", alpha = 0.7f)
        }
    }
}

private fun List<BusDeparture>.times() = joinToString(" · ") { it.time }

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun YourDayCard(day: TripDay) {
    val context = LocalContext.current
    var entries by remember(day) { mutableStateOf(DayEntryStore.load(context, day.index)) }
    var draft by remember(day) { mutableStateOf("") }
    var showPlaces by remember(day) { mutableStateOf(false) }

    fun update(new: List<DayEntry>) {
        entries = new
        DayEntryStore.save(context, day.index, new)
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val dir = File(context.filesDir, "day_photos").apply { mkdirs() }
            val file = File(dir, "day${day.index}_${System.currentTimeMillis()}.jpg")
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { input -> file.outputStream().use { input.copyTo(it) } }
            }.onSuccess { update(entries + DayEntry(DayEntry.Kind.PHOTO, file.name)) }
        }
    }
    val savedIds = remember(day, showPlaces) { CompassPreferences.saved(context) }

    DayCard("EUER TAG") {
        if (entries.isEmpty()) Line("Noch leer. Ihr entscheidet, was hier steht.", alpha = 0.7f)
        entries.forEachIndexed { index, entry ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    when (entry.kind) {
                        DayEntry.Kind.NOTE -> Line(entry.value)
                        DayEntry.Kind.PLACE -> Line("📍 " + (compassTips.firstOrNull { it.id == entry.value }?.title ?: entry.value))
                        DayEntry.Kind.PHOTO -> DayPhoto(File(File(context.filesDir, "day_photos"), entry.value))
                    }
                }
                TextButton(onClick = { update(entries.filterIndexed { i, _ -> i != index }) }) {
                    Text("×", color = KosInk.copy(alpha = 0.6f), fontSize = 20.sp)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = draft, onValueChange = { draft = it },
            placeholder = { Text("Notiz", fontFamily = KosSans, fontSize = PostcardMetrics.BODY_SP.sp) },
            modifier = Modifier.fillMaxWidth().testTag("day-note-input")
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = {
                val text = draft.trim()
                if (text.isNotEmpty()) { update(entries + DayEntry(DayEntry.Kind.NOTE, text)); draft = "" }
            }, modifier = Modifier.testTag("day-add-note")) { Text("+ Notiz", color = HomeAccent, fontFamily = KosSans, fontWeight = FontWeight.SemiBold) }
            TextButton(onClick = {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }, modifier = Modifier.testTag("day-add-photo")) { Text("+ Foto", color = HomeAccent, fontFamily = KosSans, fontWeight = FontWeight.SemiBold) }
            TextButton(onClick = { showPlaces = !showPlaces }, modifier = Modifier.testTag("day-add-place")) {
                Text("+ Ort von der Merkliste", color = HomeAccent, fontFamily = KosSans, fontWeight = FontWeight.SemiBold)
            }
        }
        if (showPlaces) {
            val tips = compassTips.filter { it.id in savedIds }
            if (tips.isEmpty()) Line("Eure Merkliste ist noch leer.", alpha = 0.7f, size = 12)
            tips.forEach { tip ->
                Text(
                    tip.title, color = KosInk, fontSize = PostcardMetrics.BODY_SP.sp, fontFamily = KosSans,
                    modifier = Modifier.fillMaxWidth().clickable {
                        update(entries + DayEntry(DayEntry.Kind.PLACE, tip.id)); showPlaces = false
                    }.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
internal fun DayPhoto(file: File) {
    val bitmap = remember(file.path) {
        runCatching {
            BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply { inSampleSize = 4 })
        }.getOrNull()
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(), contentDescription = "Euer Foto", contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(10.dp))
        )
    } else {
        Line("Foto nicht mehr vorhanden", alpha = 0.6f, size = 12)
    }
}
