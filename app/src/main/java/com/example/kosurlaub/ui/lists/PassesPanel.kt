package com.example.kosurlaub.ui.lists

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.theme.*

/** Bordkarten als Datei ablegen und am Schalter groß öffnen. Alles bleibt auf diesem Gerät. */
@Composable
internal fun PassesPanel() {
    val context = LocalContext.current
    var passes by remember { mutableStateOf(PassStore.load(context)) }
    var leg by remember { mutableStateOf("Hin") }
    var person by remember { mutableStateOf("") }
    var open by remember { mutableStateOf<Pass?>(null) }

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
        passes.forEach { pass ->
            Row(
                Modifier.fillMaxWidth().clickable { open = pass }.padding(vertical = 4.dp),
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
                TextButton({ passes = PassStore.remove(context, pass.id) }) {
                    Text("×", color = KosInk.copy(alpha = 0.6f), style = PostcardType.Heading)
                }
            }
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

    open?.let { pass ->
        // Wie bei Wallet: Bildschirm am Schalter auf volle Helligkeit, danach zurück.
        DisposableEffect(pass.id) {
            val window = (context as? Activity)?.window
            val before = window?.attributes?.screenBrightness
            window?.attributes = window?.attributes?.apply { screenBrightness = 1f }
            onDispose { window?.attributes = window?.attributes?.apply { screenBrightness = before ?: -1f } }
        }
        val barcode = remember(pass.id) { pass.payload?.let { BarcodeCodec.encode(it, pass.format.orEmpty(), 900) } }
        val data = remember(pass.id) { pass.payload?.let { Bcbp.parse(it) } }
        var showFile by remember(pass.id) { mutableStateOf(barcode == null) }
        AlertDialog(
            onDismissRequest = { open = null },
            confirmButton = { TextButton({ open = null }) { Text("Schließen", color = HomeAccent) } },
            dismissButton = if (barcode != null) {
                { TextButton({ showFile = !showFile }) { Text(if (showFile) "Barcode" else "Original", color = HomeAccent) } }
            } else null,
            title = { Text(pass.label) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    if (!showFile && barcode != null) {
                        Image(
                            barcode.asImageBitmap(), pass.label,
                            Modifier.fillMaxWidth().background(Color.White), contentScale = ContentScale.FillWidth
                        )
                        data?.let {
                            Text(it.passenger, style = PostcardType.Heading, color = KosInk, modifier = Modifier.padding(top = 12.dp))
                            Text("${it.from} → ${it.to} · ${it.flight}", style = PostcardType.Body, color = KosInk)
                            Text(
                                "${Bcbp.dayMonth(it.dayOfYear, TripData.outbound.year)} · Sitz ${it.seat} · Buchung ${it.bookingRef}",
                                style = PostcardType.Body, color = KosInk
                            )
                        }
                    } else {
                        val big = remember(pass.id) { PassStore.render(context, pass, 1400) }
                        if (big != null) {
                            Image(big.asImageBitmap(), pass.label, Modifier.fillMaxWidth(), contentScale = ContentScale.FillWidth)
                        }
                    }
                }
            }
        )
    }
}
