package com.example.kosurlaub.ui.compass
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

@Composable
internal fun CompassTipCard(
    tip: CompassTip,
    isSaved: Boolean,
    isVisited: Boolean,
    expanded: Boolean,
    showCloseButton: Boolean = true,
    onToggleExpanded: () -> Unit,
    onClose: () -> Unit,
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
    val statusColor = when (tip.kind) {
        CompassTipKind.RECOMMENDATION -> HomeAccent
        CompassTipKind.NOTE -> KosAccent2
        CompassTipKind.CAUTION -> KosWarn
    }
    val accent = categoryColor(tip.category)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (tip.kind == CompassTipKind.CAUTION) KosPanel else HomeCardColor,
                HomeCardShape
            )
            .border(1.dp, accent.copy(alpha = 0.45f), HomeCardShape)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpanded)
                .semantics {
                    role = Role.Button
                    contentDescription = "${tip.title}, ${if (expanded) "Details schließen" else "Details öffnen"}"
                }
                .testTag("compass-tip-${tip.id}")
        ) {
            Image(
                painter = tipPainter(tip, editorial.imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (expanded) 184.dp else 136.dp)
                    .clip(PostcardShape.Photo)
                    .testTag("compass-tip-image-${tip.id}-${if (expanded) "detail" else "card"}")
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(tip.category.uppercase(), color = accent, fontSize = PostcardMetrics.LABEL_SP.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tip.kind.label,
                        color = statusColor,
                        fontSize = PostcardMetrics.LABEL_SP.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    if (expanded && showCloseButton) {
                        TextButton(
                            onClick = onClose,
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp)
                                .testTag("compass-close-${tip.id}")
                                .semantics {
                                    contentDescription = "Tipp schließen und zur Tipps-Übersicht zurückkehren"
                                },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("×", color = KosInk, fontSize = 24.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            Spacer(Modifier.height(7.dp))
            Text(tip.title, color = KosInk, fontSize = PostcardMetrics.HEADING_SP.sp, fontWeight = FontWeight.SemiBold, fontFamily = KosSans)
            Spacer(Modifier.height(5.dp))
            Text(
                tip.description,
                color = KosInk.copy(alpha = 0.9f),
                fontSize = PostcardMetrics.BODY_SP.sp,
                lineHeight = 18.sp,
                fontFamily = KosSans,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                listOf(tip.location, editorial.duration, editorial.bestTime).filter(String::isNotBlank).joinToString("  ·  "),
                color = KosInk.copy(alpha = 0.75f),
                fontSize = PostcardMetrics.LABEL_SP.sp,
                fontFamily = KosSans
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (expanded) "WENIGER ANZEIGEN  ⌃" else "MEHR ANZEIGEN  ›",
                color = accent,
                fontSize = PostcardMetrics.LABEL_SP.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(
                onClick = onToggleSaved,
                modifier = Modifier.testTag("compass-save-${tip.id}")
            ) {
                Text(
                    if (isSaved) "♥ GEMERKT" else "♡ MERKEN",
                    color = if (isSaved) HomeAccent else KosInk,
                    fontSize = PostcardMetrics.LABEL_SP.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(onClick = onToggleVisited) {
                Text(if (isVisited) "✓ ERLEDIGT" else "ALS ERLEDIGT", color = if (isVisited) KosAccent2 else KosInk.copy(alpha = 0.75f), fontSize = PostcardMetrics.LABEL_SP.sp)
            }
        }

        if (expanded) {
            if (tip.tags.isNotEmpty()) {
                Text(tip.tags.joinToString("  ·  "), color = accent, fontSize = PostcardMetrics.LABEL_SP.sp, fontFamily = KosSans)
            }
            tip.reviewSummary?.let { summary ->
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(accent.copy(alpha = 0.10f), PostcardShape.Photo)
                        .border(1.dp, accent.copy(alpha = 0.30f), PostcardShape.Photo)
                        .padding(12.dp)
                ) {
                    Text(
                        "REZENSIONEN ZUSAMMENGEFASST",
                        color = accent,
                        fontSize = PostcardMetrics.LABEL_SP.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.7.sp
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        summary,
                        color = KosInk.copy(alpha = 0.9f),
                        fontSize = PostcardMetrics.LABEL_SP.sp,
                        lineHeight = 17.sp,
                        fontFamily = KosSans
                    )
                }
            }
            if (tip.hotelDetails.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(accent.copy(alpha = 0.10f), PostcardShape.Photo)
                        .border(1.dp, accent.copy(alpha = 0.30f), PostcardShape.Photo)
                        .padding(12.dp)
                        .testTag("compass-hotel-details-${tip.id}")
                ) {
                    Text(
                        "HOTEL AUF EINEN BLICK",
                        color = accent,
                        fontSize = PostcardMetrics.LABEL_SP.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.7.sp
                    )
                    tip.hotelDetails.forEachIndexed { index, detail ->
                        Spacer(Modifier.height(if (index == 0) 10.dp else 12.dp))
                        Text(
                            detail.title,
                            color = KosInk.copy(alpha = 0.75f),
                            fontSize = PostcardMetrics.LABEL_SP.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            detail.text,
                            color = KosInk.copy(alpha = 0.9f),
                            fontSize = PostcardMetrics.LABEL_SP.sp,
                            lineHeight = 17.sp,
                            fontFamily = KosSans
                        )
                    }
                }
            } else {
                Spacer(Modifier.height(12.dp))
                CompassFactRow("DAUER", editorial.duration)
                CompassFactRow("BESTE ZEIT", editorial.bestTime)
                CompassFactRow("AB HOTEL", editorial.fromHotel)
                CompassFactRow("ANREISE", editorial.transport)

                Spacer(Modifier.height(10.dp))
                PostcardLabel("IM SEPTEMBER")
                Text(editorial.septemberNote, color = KosInk.copy(alpha = 0.9f), fontSize = PostcardMetrics.LABEL_SP.sp, lineHeight = 17.sp, fontFamily = KosSans)

                Spacer(Modifier.height(12.dp))
                CompassFactRow("GUT KOMBINIERBAR", editorial.combination)
                CompassFactRow("RÜCKFAHRT", editorial.returnTip)
                CompassFactRow("VOR ORT", editorial.facilities)
            }

            Spacer(Modifier.height(10.dp))
            Text("EHRLICH EINGESCHÄTZT", color = KosWarn, fontSize = PostcardMetrics.LABEL_SP.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
            Text(editorial.counterRecommendation, color = Color(0xFFD9C1A8), fontSize = PostcardMetrics.LABEL_SP.sp, lineHeight = 17.sp, fontFamily = KosSans)

            Spacer(Modifier.height(12.dp))
            if (tip.mapsUrl != null) {
                OutlinedButton(
                    onClick = onOpenMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("compass-map-${tip.id}"),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.75f))
                ) {
                    Text(
                        "IN GOOGLE MAPS ÖFFNEN  ↗",
                        color = accent,
                        fontSize = PostcardMetrics.LABEL_SP.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            PostcardTextField(
                value = personalNote,
                onValueChange = { personalNote = it },
                label = { Text("Eigene Notiz") },
                placeholder = { Text("Treffpunkt, Uhrzeit oder Erinnerung") },
                minLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compass-note-${tip.id}")
            )

            Spacer(Modifier.height(10.dp))
            Text(
                if (tip.source == CompassTipSource.COMMUNITY) "PERSÖNLICHE ERFAHRUNG" else "RECHERCHIERTER HINWEIS · STAND ${editorial.checkedAt}",
                color = KosInk.copy(alpha = 0.5f),
                fontSize = PostcardMetrics.LABEL_SP.sp,
                fontWeight = FontWeight.Bold
            )
            editorial.validUntil?.let {
                Text(
                    "GÜLTIGKEIT · $it",
                    color = KosWarn,
                    fontSize = PostcardMetrics.LABEL_SP.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (tip.sourceUrl != null) {
                Text(
                    "${tip.source.label} · QUELLE ↗",
                    color = KosInk.copy(alpha = 0.6f),
                    fontSize = PostcardMetrics.LABEL_SP.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable(onClick = onOpenSource)
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
internal fun CompassFactRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, color = KosInk.copy(alpha = 0.5f), fontSize = PostcardMetrics.LABEL_SP.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(116.dp))
        Text(value, color = KosInk.copy(alpha = 0.9f), fontSize = PostcardMetrics.LABEL_SP.sp, lineHeight = 16.sp, fontFamily = KosSans, modifier = Modifier.weight(1f))
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
