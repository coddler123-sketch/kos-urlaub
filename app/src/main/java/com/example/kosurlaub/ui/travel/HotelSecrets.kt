package com.example.kosurlaub.ui.travel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.theme.HomeAccent
import com.example.kosurlaub.ui.theme.PostcardLabel
import com.example.kosurlaub.ui.theme.PostcardTextField
import com.example.kosurlaub.ui.theme.PostcardType

private const val PREFS = "kos_hotel_secrets"
private val fields = listOf("booking" to "Buchungsnummer", "room" to "Zimmernummer", "wifi" to "WLAN-Passwort")

/** Eigene Angaben, die nicht im Code stehen: werden vom Nutzer eingetragen und nur lokal gespeichert. */
@Composable
internal fun HotelSecretsCard() {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val values = remember { fields.associate { (k, _) -> k to mutableStateOf(prefs(context).getString(k, "").orEmpty()) } }

    fun copy(label: String, text: String) {
        clipboard.setText(AnnotatedString(text))
        Toast.makeText(context, "$label kopiert", Toast.LENGTH_SHORT).show()
    }

    TravelCardContainer {
        PostcardLabel("Eure Hotel-Daten", Modifier.testTag("hotel-secrets-card"))
        fields.forEach { (key, label) ->
            var value by values.getValue(key)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                PostcardTextField(
                    value = value,
                    onValueChange = { value = it; prefs(context).edit().putString(key, it.trim()).apply() },
                    label = { Text(label) },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("hotel-secret-$key")
                )
                TextButton(onClick = { copy(label, value) }, enabled = value.isNotBlank()) {
                    Text("Kopieren", color = HomeAccent)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
            TextButton(onClick = {
                copy("Adresse", "${TripData.HOTEL_NAME}, ${TripData.HOTEL_PLACE}, ${TripData.HOTEL_POSTAL_CODE} Kos")
            }) { Text("ADRESSE KOPIEREN", style = PostcardType.Label, color = HomeAccent) }
            TextButton(onClick = {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + TripData.HOTEL_PHONE.replace(" ", ""))))
            }) { Text("ANRUFEN", style = PostcardType.Label, color = HomeAccent) }
        }
    }
}

private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
