package com.example.kosurlaub.ui.compass
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

internal data class CompassCategory(
    val title: String,
    val description: String,
    @param:DrawableRes val imageRes: Int
)

@Composable
internal fun CompassOverviewHeader(count: Int, onRandomTip: () -> Unit = {}) {
    Row(verticalAlignment = Alignment.Bottom) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Tipps & Orte", style = PostcardType.Title, color = KosInk)
            PostcardLabel("$count ausgewählte Tipps")
        }
        PostcardChip("Überrascht uns!", false, onRandomTip, Modifier.testTag("compass-random-tip-button"))
    }
}

@Composable
internal fun CompassDayPlanCard(tips: List<CompassTip>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x2924A7A4), HomeCardShape)
            .border(1.dp, HomeAccent.copy(alpha = 0.65f), HomeCardShape)
            .padding(15.dp)
            .testTag("compass-day-plan")
    ) {
        PostcardLabel("EUER KLEINER TAGESPLAN")
        Spacer(Modifier.height(8.dp))
        tips.forEachIndexed { index, tip ->
            val editorial = editorialFor(tip)
            Text(
                "${index + 1}. ${editorial.bestTime} · ${tip.title}",
                color = KosInk,
                fontSize = PostcardMetrics.BODY_SP.sp,
                lineHeight = 18.sp,
                fontFamily = KosSans
            )
        }
        Spacer(Modifier.height(6.dp))
        Text("Aus euren gemerkten Tipps · maximal drei Stationen", color = KosInk.copy(alpha = 0.6f), fontSize = PostcardMetrics.LABEL_SP.sp)
    }
}
@Composable
internal fun CompassSearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    PostcardTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("compass-search"),
        singleLine = true,
        label = { Text("Tipps durchsuchen") },
        placeholder = { Text("z. B. Tigaki, Bus oder ruhig") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                TextButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                        .semantics { contentDescription = "Suche leeren" },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("×", color = KosInk, fontSize = 22.sp)
                }
            }
        }
    )
}

@Composable
internal fun CompassCommunityCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .heightIn(min = 132.dp)
            .background(HomeCardColor, HomeCardShape)
            .border(1.dp, HomeCardBorder, HomeCardShape)
            .clickable(onClick = onClick)
            .testTag("community-link")
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Facebook", color = KosInk, fontSize = PostcardMetrics.HEADING_SP.sp, fontWeight = FontWeight.SemiBold, fontFamily = KosSans)
            Spacer(Modifier.height(6.dp))
            Text("Weitere Erfahrungen aus der Kos-Community", color = KosInk.copy(alpha = 0.75f), fontSize = PostcardMetrics.LABEL_SP.sp, lineHeight = 16.sp, fontFamily = KosSans)
        }
        Text("COMMUNITY  ↗", style = PostcardType.Label, color = HomeAccent)
    }
}

/** Detailansicht eines Tipps: Foto, Titel, Fakten, Merken/Erledigt, Karte und eigene Notiz. */
@Composable
internal fun CompassTipCard(
    tip: CompassTip,
    isSaved: Boolean,
    isVisited: Boolean,
    onToggleSaved: () -> Unit,
    onToggleVisited: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenSource: () -> Unit
) {
    val context = LocalContext.current
    val editorial = editorialFor(tip)
    var personalNote by remember(tip.id) {
        mutableStateOf(CompassPreferences.note(context, tip.id))
    }
    LaunchedEffect(tip.id, personalNote) {
        delay(350)
        CompassPreferences.saveNote(context, tip.id, personalNote)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PostcardShape.Panel)
            .background(HomeCardColor)
            .border(1.dp, HomeCardBorder, PostcardShape.Panel)
            .testTag("compass-tip-${tip.id}")
    ) {
        Image(
            painter = tipPainter(tip, editorial.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .testTag("compass-tip-image-${tip.id}-detail")
        )
        Column(modifier = Modifier.padding(PostcardSpace.Panel), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StampChip(tip.kind.label)
            Text(tip.title, style = PostcardType.Title, color = KosInk)
            Text(tip.description, style = PostcardType.Body, color = KosInk)
            val meta = listOf(tip.location, editorial.duration, editorial.bestTime).filter(String::isNotBlank)
            if (meta.isNotEmpty()) {
                Text(meta.joinToString("  ·  "), style = PostcardType.Label, color = KosInk.copy(alpha = 0.75f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PostcardChip(
                    if (isSaved) "♥ Gemerkt" else "♡ Merken", isSaved, onToggleSaved,
                    Modifier.testTag("compass-save-${tip.id}")
                )
                PostcardChip(if (isVisited) "✓ Erledigt" else "Als erledigt", isVisited, onToggleVisited)
            }
            if (tip.tags.isNotEmpty()) {
                Text(tip.tags.joinToString("  ·  "), style = PostcardType.Label, color = KosInk.copy(alpha = 0.75f))
            }
            tip.reviewSummary?.let { summary ->
                TipPanel("Rezensionen zusammengefasst") {
                    Text(summary, style = PostcardType.Body, color = KosInk)
                }
            }
            if (tip.hotelDetails.isNotEmpty()) {
                TipPanel("Hotel auf einen Blick", Modifier.testTag("compass-hotel-details-${tip.id}")) {
                    tip.hotelDetails.forEachIndexed { index, detail ->
                        if (index > 0) Spacer(Modifier.height(6.dp))
                        PostcardLabel(detail.title)
                        Text(detail.text, style = PostcardType.Body, color = KosInk)
                    }
                }
            } else {
                Column {
                    CompassFactRow("Dauer", editorial.duration)
                    CompassFactRow("Beste Zeit", editorial.bestTime)
                    CompassFactRow("Ab Hotel", editorial.fromHotel)
                    CompassFactRow("Anreise", editorial.transport)
                }
                TipPanel("Im September") {
                    Text(editorial.septemberNote, style = PostcardType.Body, color = KosInk)
                }
                Column {
                    CompassFactRow("Gut kombinierbar", editorial.combination)
                    CompassFactRow("Rückfahrt", editorial.returnTip)
                    CompassFactRow("Vor Ort", editorial.facilities)
                }
            }
            TipPanel("Ehrlich eingeschätzt", labelColor = KosStamp) {
                Text(editorial.counterRecommendation, style = PostcardType.Body, color = KosInk)
            }
            if (tip.mapsUrl != null) {
                Button(
                    onClick = onOpenMap,
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("compass-map-${tip.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = KosInk, contentColor = KosBg)
                ) {
                    Text("IN GOOGLE MAPS ÖFFNEN  ↗", style = PostcardType.Label)
                }
            }
            PostcardTextField(
                value = personalNote,
                onValueChange = { personalNote = it },
                label = { Text("Eigene Notiz") },
                placeholder = { Text("Treffpunkt, Uhrzeit oder Erinnerung") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth().testTag("compass-note-${tip.id}")
            )
            Column {
                Text(
                    if (tip.source == CompassTipSource.COMMUNITY) "PERSÖNLICHE ERFAHRUNG" else "RECHERCHIERTER HINWEIS · STAND ${editorial.checkedAt}",
                    style = PostcardType.Label, color = KosInk.copy(alpha = 0.6f)
                )
                editorial.validUntil?.let {
                    Text("GÜLTIGKEIT · $it", style = PostcardType.Label, color = KosStamp)
                }
                if (tip.sourceUrl != null) {
                    Text(
                        "${tip.source.label} · QUELLE ↗",
                        style = PostcardType.Label, color = HomeAccent,
                        modifier = Modifier.clickable(onClick = onOpenSource).padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

/** Abgesetzter Block mit Überschrift innerhalb der Tipp-Detailansicht. */
@Composable
private fun TipPanel(
    title: String,
    modifier: Modifier = Modifier,
    labelColor: Color = KosInk,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(PostcardShape.Photo)
            .background(KosPanelHigh)
            .padding(PostcardSpace.Panel),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(title.uppercase(), style = PostcardType.Label, color = labelColor)
        content()
    }
}

@Composable
internal fun CompassFactRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        PostcardLabel(label, Modifier.width(120.dp))
        Text(value, style = PostcardType.Body, color = KosInk, modifier = Modifier.weight(1f))
    }
}

@Composable
internal fun categoryColor(category: String): Color = when (category) {
    "Tigaki" -> Color(0xFF64D8CB)
    "Essen" -> Color(0xFFE8B96A)
    "Strände" -> Color(0xFF70B8E8)
    "Ausflüge" -> Color(0xFFE58B6B)
    "Mobilität" -> Color(0xFF8DD7E3)
    "Unterkünfte" -> Color(0xFF93C98B)
    "Supermärkte" -> Color(0xFF79C98D)
    "Mode & Accessoires" -> Color(0xFFE0A36C)
    "Souvenirs" -> Color(0xFFD6A4E8)
    "Regionale Produkte" -> Color(0xFFD9A86C)
    else -> HomeAccent
}

/** Privates Foto zum Tipp, falls geladen, sonst das freie Bild aus den Ressourcen. */
@Composable
internal fun tipPainter(tip: CompassTip, @DrawableRes fallback: Int): androidx.compose.ui.graphics.painter.Painter {
    val context = androidx.compose.ui.platform.LocalContext.current
    val photo = remember(tip.id, PrivateImages.version.intValue) {
        PrivateImages.tipBitmap(context, tip.id)?.asImageBitmap()
    }
    return photo?.let { androidx.compose.ui.graphics.painter.BitmapPainter(it) } ?: painterResource(fallback)
}
