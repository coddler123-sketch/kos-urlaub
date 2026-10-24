package com.example.kosurlaub.ui.lists

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.home.AppClock
import com.example.kosurlaub.ui.theme.*

/** Bordkarten aller Reisenden, nach Hin- und Rückflug geordnet. Alles bleibt auf diesem Gerät. [top] und [bottom] rahmen die Liste im Reise-Tab ein. */
@Composable
fun PassesScreen(
    padding: PaddingValues,
    scrollToTopRequest: Int,
    top: LazyListScope.() -> Unit = {},
    bottom: LazyListScope.() -> Unit = {}
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    LaunchedEffect(scrollToTopRequest) {
        if (scrollToTopRequest > 0) listState.animateScrollToItem(0)
    }
    var passes by remember { mutableStateOf(PassStore.load(context)) }
    var open by remember { mutableStateOf<Pair<List<Pass>, Int>?>(null) }
    var toDelete by remember { mutableStateOf<Pass?>(null) }
    var showPast by remember { mutableStateOf(false) }
    var leg by remember { mutableStateOf("Hin") }
    var person by remember { mutableStateOf("") }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val updated = PassStore.add(context, uri, PassStore.label(leg, person))
            if (updated == null) {
                Toast.makeText(context, "Das ließ sich nicht lesen. Foto, Screenshot oder PDF wählen.", Toast.LENGTH_LONG).show()
            } else {
                passes = updated
                person = ""
            }
        }
    }

    val now = AppClock.nowMillis()
    val (past, current) = passes.partition { PassTiming.isPast(it, now) }

    fun group(source: List<Pass>, legKey: String) = source.filter { (PassTiming.leg(it) ?: "") == legKey }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().testTag("passes-screen").padding(padding),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        top()
        item {
            Column {
                Text("Bordkarten", style = PostcardType.Title, color = KosInk)
                PostcardLabel("Für alle Reisenden")
            }
        }
        if (passes.isEmpty()) {
            item {
                PaperPanel {
                    Text(
                        "Noch keine Bordkarte. Lade ein Foto, einen Screenshot oder das PDF der Bordkarte. Die App liest den Barcode und zeigt ihn groß mit den Flugdaten.",
                        style = PostcardType.Body, color = KosInk
                    )
                }
            }
        }
        for (key in listOf("Hin", "Rück", "")) {
            val items = group(current, key)
            if (items.isEmpty()) continue
            item(key = "section-$key") {
                PaperPanel {
                    PostcardLabel(sectionTitle(key))
                    items.forEachIndexed { i, pass ->
                        PassRow(pass, { open = items to i }, { toDelete = pass })
                    }
                }
            }
        }
        if (past.isNotEmpty()) {
            item(key = "past") {
                PaperPanel {
                    TextButton({ showPast = !showPast }, Modifier.testTag("passes-past-toggle")) {
                        Text(
                            if (showPast) "VERGANGENE FLÜGE AUSBLENDEN" else "VERGANGENE FLÜGE (${past.size})",
                            style = PostcardType.Label, color = HomeAccent
                        )
                    }
                    if (showPast) {
                        for (key in listOf("Hin", "Rück", "")) {
                            val items = group(past, key)
                            items.forEachIndexed { i, pass -> PassRow(pass, { open = items to i }, { toDelete = pass }) }
                        }
                    }
                }
            }
        }
        item {
            PaperPanel(Modifier.testTag("passes-add-panel")) {
                PostcardLabel("Bordkarte hinzufügen")
                Text(
                    "Für jede Person eine Datei. Die App erkennt Strecke und Namen am Barcode selbst. Nur ohne Barcode zählt die Auswahl unten.",
                    style = PostcardType.Body, color = KosInk.copy(alpha = 0.7f), modifier = Modifier.padding(top = 4.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
                    PostcardChip("Hin", leg == "Hin", { leg = "Hin" })
                    PostcardChip("Rück", leg == "Rück", { leg = "Rück" })
                }
                PostcardTextField(
                    value = person, onValueChange = { person = it }, singleLine = true,
                    placeholder = { Text("Name (nur ohne Barcode)", style = PostcardType.Body) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                TextButton({ picker.launch(arrayOf("image/*", "application/pdf")) }, Modifier.testTag("passes-add")) {
                    Text("DATEI WÄHLEN", style = PostcardType.Label, color = HomeAccent)
                }
            }
        }
        bottom()
    }

    open?.let { (list, start) -> PassView(list, start, onClose = { open = null }) }

    toDelete?.let { pass ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Bordkarte löschen?") },
            text = { Text(pass.label) },
            confirmButton = {
                TextButton({ passes = PassStore.remove(context, pass.id); toDelete = null }) { Text("Löschen", color = HomeAccent) }
            },
            dismissButton = { TextButton({ toDelete = null }) { Text("Abbrechen", color = HomeAccent) } }
        )
    }
}

private fun sectionTitle(key: String): String {
    val flight = PassTiming.flight(key.ifEmpty { null }) ?: return "Weitere"
    val name = if (key == "Hin") "Hinflug" else "Rückflug"
    return "$name · ${flight.fromAirport} → ${flight.toAirport} · %02d.%02d.".format(flight.day, flight.month)
}

@Composable
private fun PassRow(pass: Pass, onOpen: () -> Unit, onRemove: () -> Unit) {
    val context = LocalContext.current
    val data = remember(pass.id) { pass.payload?.let { Bcbp.parse(it) } }
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onOpen).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val thumb = remember(pass.id) { PassStore.render(context, pass, 160) }
        if (thumb != null) {
            Image(thumb.asImageBitmap(), null, Modifier.size(56.dp).padding(end = 12.dp), contentScale = ContentScale.Fit)
        }
        Column(Modifier.weight(1f)) {
            Text(pass.label.substringAfter(" · ", pass.label), style = PostcardType.Heading, color = KosInk)
            val detail = listOfNotNull(
                data?.let { "${it.flight}${if (it.seat.isNotEmpty()) " · Sitz ${it.seat}" else ""}" },
                if (pass.payload != null) "Barcode erkannt" else "Nur Bild"
            ).joinToString(" · ")
            Text(detail, style = PostcardType.Label, color = KosInk.copy(alpha = 0.7f))
        }
        TextButton(onRemove) { Text("×", color = KosInk.copy(alpha = 0.6f), style = PostcardType.Heading) }
    }
}
