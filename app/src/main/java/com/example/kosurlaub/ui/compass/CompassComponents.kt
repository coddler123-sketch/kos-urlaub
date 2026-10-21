package com.example.kosurlaub.ui.compass
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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

/** Kopf einer Guide-Unterseite: Zurück-Pfeil zur Startseite, Titel und optional eine Aktion rechts. */
@Composable
internal fun GuideBackHeader(title: String, onBack: () -> Unit, action: (@Composable () -> Unit)? = null) {
    Column {
        TextButton(onClick = onBack, modifier = Modifier.testTag("guide-back")) {
            Text(stringResource(R.string.travel_back), color = HomeAccent)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = PostcardType.Title, color = KosInk, modifier = Modifier.weight(1f))
            action?.invoke()
        }
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
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    PostcardTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
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
        val photo = rememberTipImage(tip, editorial.imageRes, DETAIL_IMAGE_WIDTH_PX)
        Box(
            Modifier.fillMaxWidth().height(220.dp).background(KosPanelHigh)
                .testTag("compass-tip-image-${tip.id}-detail")
        ) {
            photo?.let {
                Image(it, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
        }
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
            TipDayPlanner(tip.id)
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

/** Zielbreiten in Pixeln: Raster-Kachel (halbe Bildschirmbreite) und Detailansicht (volle Breite). */
internal const val CARD_IMAGE_WIDTH_PX = 640
internal const val DETAIL_IMAGE_WIDTH_PX = 1200

private val tipImageCache = object : android.util.LruCache<String, ImageBitmap>(24 * 1024 * 1024) {
    override fun sizeOf(key: String, value: ImageBitmap) = value.width * value.height * 4
}

private fun decodeScaledResource(context: android.content.Context, @DrawableRes res: Int, targetWidth: Int): android.graphics.Bitmap? {
    val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
    android.graphics.BitmapFactory.decodeResource(context.resources, res, bounds)
    val options = android.graphics.BitmapFactory.Options().apply {
        inSampleSize = (bounds.outWidth / targetWidth).coerceAtLeast(1)
    }
    return android.graphics.BitmapFactory.decodeResource(context.resources, res, options)
}

/**
 * Tipp-Bild im Hintergrund und nur so groß wie nötig laden (privates Foto, sonst freies Bild),
 * damit das Scrollen im Raster nicht an riesigen Bitmaps hakt. Null, solange es lädt.
 */
@Composable
internal fun rememberTipImage(tip: CompassTip, @DrawableRes fallback: Int, targetWidthPx: Int): ImageBitmap? {
    val context = androidx.compose.ui.platform.LocalContext.current
    val version = PrivateImages.version.intValue
    return androidx.compose.runtime.produceState<ImageBitmap?>(null, tip.id, fallback, targetWidthPx, version) {
        val key = "${tip.id}|$fallback|$targetWidthPx|$version"
        val cached = tipImageCache.get(key)
        value = cached ?: kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            (PrivateImages.tipBitmap(context, tip.id, targetWidthPx)
                ?: decodeScaledResource(context, fallback, targetWidthPx))?.asImageBitmap()
        }?.also { tipImageCache.put(key, it) }
    }.value
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

/** Tage wählen, an denen der Tipp im Tagesplan auf Heute steht. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TipDayPlanner(tipId: String) {
    val context = LocalContext.current
    var planned by remember(tipId) { mutableStateOf(DayEntryStore.plannedDays(context, tipId)) }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        PostcardLabel("Im Tagesplan")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TripDays.all.forEach { day ->
                PostcardChip(
                    "${day.weekday} ${day.day}.", day.index in planned,
                    {
                        DayEntryStore.togglePlace(context, day.index, tipId)
                        planned = DayEntryStore.plannedDays(context, tipId)
                    },
                    Modifier.testTag("compass-plan-${tipId}-${day.index}")
                )
            }
        }
    }
}
