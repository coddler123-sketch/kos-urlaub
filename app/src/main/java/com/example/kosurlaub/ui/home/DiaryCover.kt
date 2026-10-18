package com.example.kosurlaub.ui.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.ui.theme.HomeAccent
import com.example.kosurlaub.ui.theme.HomeCardBorder
import com.example.kosurlaub.ui.theme.HomeCardColor
import com.example.kosurlaub.ui.theme.HomeCardShape
import com.example.kosurlaub.ui.theme.KosInk
import com.example.kosurlaub.ui.theme.KosSans
import com.example.kosurlaub.ui.theme.KosSerif

/** Tagesfoto als leicht gekipptes Polaroid. Die Rahmenfarbe bleibt in beiden Modi hell wie Fotopapier. */
@Composable
fun PolaroidPhoto(imageRes: Int, caption: String, modifier: Modifier = Modifier) {
    if (imageRes == 0) return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .rotate(-2f)
            .shadow(10.dp, RoundedCornerShape(3.dp))
            .background(Color(0xFFFFFDF8), RoundedCornerShape(3.dp))
            .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 12.dp)
            .testTag("home-polaroid")
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f)
        )
        Text(
            text = caption,
            color = Color(0xFF2B3A30),
            fontFamily = KosSerif,
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        )
    }
}

/** Eine Zeile der freien Checkliste. */
data class ChecklistItem(val text: String, val done: Boolean)

/** Lokale Ablage der Checkliste, eine Zeile je Punkt: "0|Text" oder "1|Text". */
object ChecklistStore {
    private const val PREFS = "kos_checklist"
    private const val KEY = "items"

    fun encode(items: List<ChecklistItem>): String =
        items.joinToString("\n") { (if (it.done) "1|" else "0|") + it.text.replace("\n", " ") }

    fun decode(raw: String?): List<ChecklistItem> =
        raw.orEmpty().lines().filter { it.length > 2 && it[1] == '|' }
            .map { ChecklistItem(it.substring(2), it[0] == '1') }

    fun load(context: Context): List<ChecklistItem> =
        decode(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null))

    fun save(context: Context, items: List<ChecklistItem>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, encode(items)).apply()
    }
}

/** Freie Checkliste: keine Vorgaben, nur eigene Punkte. */
@Composable
fun FreeChecklistCard() {
    val context = LocalContext.current
    var items by remember { mutableStateOf(ChecklistStore.load(context)) }
    var draft by remember { mutableStateOf("") }

    fun update(new: List<ChecklistItem>) {
        items = new
        ChecklistStore.save(context, new)
    }

    fun add() {
        val text = draft.trim()
        if (text.isNotEmpty()) {
            update(items + ChecklistItem(text, false))
            draft = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeCardColor)
            .border(1.dp, HomeCardBorder, HomeCardShape)
            .padding(16.dp)
            .testTag("home-checklist")
    ) {
        Text(
            "EURE LISTE",
            color = HomeAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold,
            fontFamily = KosSans, letterSpacing = 1.sp
        )
        if (items.isEmpty()) {
            Text(
                "Noch leer. Schreibt auf, was ihr nicht vergessen wollt.",
                color = KosInk.copy(alpha = 0.7f), fontSize = 13.sp, fontFamily = KosSans,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        items.forEachIndexed { index, item ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = item.done,
                    onCheckedChange = { update(items.toMutableList().also { l -> l[index] = item.copy(done = it) }) },
                    colors = CheckboxDefaults.colors(checkedColor = HomeAccent, uncheckedColor = KosInk.copy(alpha = 0.5f))
                )
                Text(
                    item.text,
                    color = KosInk.copy(alpha = if (item.done) 0.5f else 0.95f),
                    fontSize = 14.sp, fontFamily = KosSans,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { update(items.filterIndexed { i, _ -> i != index }) }) {
                    Text("×", color = KosInk.copy(alpha = 0.6f), fontSize = 20.sp)
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Neuer Punkt", fontFamily = KosSans, fontSize = 13.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { add() }),
                modifier = Modifier.weight(1f).testTag("home-checklist-input")
            )
            TextButton(onClick = ::add, modifier = Modifier.testTag("home-checklist-add")) {
                Text("Hinzufügen", color = HomeAccent, fontFamily = KosSans, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
