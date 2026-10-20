package com.example.kosurlaub.ui.compass

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.ui.theme.*

internal data class QuickFilter(val key: String, val label: String)
internal data class QuickFilterGroup(val title: String, val filters: List<QuickFilter>)

/**
 * Quick-Filter im Filter-Sheet, nach Frage gruppiert. Die Schlüssel gehören zu [matchesCompassQuickFilter].
 * "Essen" und "Rund ums Hotel" fehlen mit Absicht: Sie doppeln die Kategorie "Essen" und den Ort "Tigaki".
 */
internal val quickFilterGroups = listOf(
    QuickFilterGroup(
        "Ort",
        listOf(
            QuickFilter("📍 Tigaki", "Tigaki"),
            QuickFilter("🏛️ Kos-Stadt", "Kos-Stadt"),
            QuickFilter("🏰 Süden & Inland", "Süden & Inland"),
            QuickFilter("🚤 Bootsausflüge", "Bootsausflüge")
        )
    ),
    QuickFilterGroup(
        "Anfahrt",
        listOf(
            QuickFilter("Zu Fuß", "Zu Fuß"),
            QuickFilter("15–30 Min Bus/Taxi", "Bus oder Taxi, 15–30 Min"),
            QuickFilter("Ohne Auto", "Ohne Auto")
        )
    ),
    QuickFilterGroup("Dauer", listOf(QuickFilter("Halber Tag", "Halber Tag"), QuickFilter("Tagesausflug", "Tagesausflug"))),
    QuickFilterGroup("Stimmung", listOf(QuickFilter("Ruhig", "Ruhig")))
)

/** Eine Zeile: Kategorien als Chips (wischbar) und rechts fest der Knopf "Filter". */
@Composable
internal fun CompassChipRow(
    categories: List<CompassCategory>,
    selected: String?,
    onSelected: (String?) -> Unit,
    filterActive: Boolean,
    onOpenFilter: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(modifier = Modifier.weight(1f).testTag("compass-category-row"), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                PostcardChip("Alle", selected == null, { onSelected(null) }, Modifier.testTag("compass-category-filter-all"))
            }
            items(categories, key = { it.title }) { category ->
                PostcardChip(
                    category.title, selected == category.title, { onSelected(category.title) },
                    Modifier.testTag("compass-category-filter-${category.title}")
                )
            }
        }
        PostcardChip(
            label = if (filterActive) "Filter · 1" else "Filter",
            selected = filterActive,
            onClick = onOpenFilter,
            modifier = Modifier.testTag("compass-filter-button")
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun CompassFilterSheet(
    selected: String?,
    onSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = KosBg,
        modifier = Modifier.testTag("compass-filter-sheet")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = PostcardSpace.Screen).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(PostcardSpace.Gap)
        ) {
            Text("Filter", style = PostcardType.Heading, color = KosInk)
            quickFilterGroups.forEach { group ->
                PostcardLabel(group.title)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    group.filters.forEach { filter ->
                        PostcardChip(
                            label = filter.label,
                            selected = selected == filter.key,
                            onClick = { onSelected(if (selected == filter.key) null else filter.key) },
                            modifier = Modifier.testTag("compass-filter-${filter.key}")
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PostcardChip("Zurücksetzen", false, { onSelected(null) })
                PostcardChip("Fertig", true, onDismiss, Modifier.testTag("compass-filter-done"))
            }
        }
    }
}

internal const val SAVED_DESCRIPTION = "Tipp gemerkt, tippen zum Entfernen"
internal const val UNSAVED_DESCRIPTION = "Tipp merken"

/** Foto-Karte eines Tipps im 2-Spalten-Raster; Herz oben rechts merkt den Tipp. */
@Composable
internal fun CompassTipPhotoCard(
    tip: CompassTip,
    isSaved: Boolean,
    isVisited: Boolean,
    onOpen: () -> Unit,
    onToggleSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val editorial = editorialFor(tip)
    Box(
        modifier = modifier
            .height(160.dp)
            .clip(PostcardShape.Photo)
            .clickable(onClick = onOpen)
            .testTag("compass-tip-${tip.id}")
    ) {
        val photo = rememberTipImage(tip, editorial.imageRes, CARD_IMAGE_WIDTH_PX)
        Box(Modifier.fillMaxSize().background(KosPanelHigh).testTag("compass-tip-image-${tip.id}-card")) {
            photo?.let {
                Image(it, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))))
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(PostcardMetrics.PHOTO_TEXT_PADDING_DP.dp)
        ) {
            Text(
                tip.title,
                style = PostcardType.Heading.copy(fontSize = PostcardMetrics.BODY_SP.sp, lineHeight = 18.sp),
                color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Text(
                listOfNotNull(tip.category, if (isVisited) "besucht ✓" else null).joinToString(" · "),
                style = PostcardType.Label, color = Color.White.copy(alpha = 0.9f), maxLines = 1
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp)
                .clickable(onClick = onToggleSaved)
                .semantics {
                    role = Role.Button
                    contentDescription = if (isSaved) SAVED_DESCRIPTION else UNSAVED_DESCRIPTION
                }
                .testTag("compass-save-${tip.id}"),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(34.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isSaved) "♥" else "♡", color = Color.White, fontSize = 20.sp)
            }
        }
    }
}
