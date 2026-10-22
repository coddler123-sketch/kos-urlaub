package com.example.kosurlaub.ui.lists

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.kosurlaub.ui.home.AppClock
import com.example.kosurlaub.ui.theme.*

/** Bordkarten als Datei ablegen und am Schalter groß öffnen. Alles bleibt auf diesem Gerät. */
@Composable
internal fun PassesPanel() {
    val context = LocalContext.current
    var passes by remember { mutableStateOf(PassStore.load(context)) }
    var leg by remember { mutableStateOf("Hin") }
    var person by remember { mutableStateOf("") }
    var open by remember { mutableStateOf<Pass?>(null) }
    var showPast by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val updated = PassStore.add(context, uri, PassStore.label(leg, person))
            if (updated == null) {
                Toast.makeText(context, "Das ließ sich nicht lesen. Foto oder PDF wählen.", Toast.LENGTH_LONG).show()
            } else {
                passes = updated
            }
        }
    }

    PaperPanel(Modifier.testTag("passes-panel")) {
        PostcardLabel("Bordkarten")
        Text(
            "Für jeden Flug ein Foto, Screenshot oder PDF der Bordkarte. Die App liest den Barcode und zeigt ihn groß mit den Flugdaten. Alles bleibt nur auf diesem Gerät.",
            style = PostcardType.Body, color = KosInk.copy(alpha = 0.7f), modifier = Modifier.padding(top = 4.dp)
        )
        val now = AppClock.nowMillis()
        val (past, current) = passes.partition { PassTiming.isPast(it, now) }
        current.forEach { PassRow(it, { open = it }, { passes = PassStore.remove(context, it.id) }) }
        if (past.isNotEmpty()) {
            TextButton({ showPast = !showPast }, Modifier.testTag("passes-past-toggle")) {
                Text(
                    (if (showPast) "VERGANGENE FLÜGE AUSBLENDEN" else "VERGANGENE FLÜGE (${past.size})"),
                    style = PostcardType.Label, color = HomeAccent
                )
            }
            if (showPast) past.forEach { PassRow(it, { open = it }, { passes = PassStore.remove(context, it.id) }) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
            PostcardChip("Hin", leg == "Hin", { leg = "Hin" })
            PostcardChip("Rück", leg == "Rück", { leg = "Rück" })
        }
        PostcardTextField(
            value = person, onValueChange = { person = it }, singleLine = true,
            placeholder = { Text("Name (optional)", style = PostcardType.Body) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        TextButton({ picker.launch(arrayOf("image/*", "application/pdf")) }, Modifier.testTag("passes-add")) {
            Text("DATEI WÄHLEN", style = PostcardType.Label, color = HomeAccent)
        }
    }

    open?.let { pass -> PassView(pass, onClose = { open = null }) }
}

@Composable
private fun PassRow(pass: Pass, onOpen: () -> Unit, onRemove: () -> Unit) {
    val context = LocalContext.current
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onOpen).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val thumb = remember(pass.id) { PassStore.render(context, pass, 160) }
        if (thumb != null) {
            Image(thumb.asImageBitmap(), null, Modifier.size(56.dp).padding(end = 12.dp), contentScale = ContentScale.Fit)
        }
        Column(Modifier.weight(1f)) {
            Text(pass.label, style = PostcardType.Body, color = KosInk)
            if (pass.payload != null) {
                Text("Barcode erkannt", style = PostcardType.Label, color = KosInk.copy(alpha = 0.7f))
            }
        }
        TextButton(onRemove) { Text("×", color = KosInk.copy(alpha = 0.6f), style = PostcardType.Heading) }
    }
}
