package com.example.kosurlaub.ui.compass

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.kosurlaub.ui.theme.*

/** Praktische Infos auf der Guide-Startseite: Seitenschlüssel, Titel, Kurzbeschreibung. */
private val practicalTiles = listOf(
    Triple("TRAVEL:HELP", "SOS & Hilfe", "Notruf 112, Arzt, Apotheke"),
    Triple("TRAVEL:MOBILITY", "Bus & Mobil", "Fahrplan Tigaki – Kos-Stadt"),
    Triple("TRAVEL:TODAY", "Reise & Meer", "Hotel, Flug, Meer und Wellen"),
    Triple("TRAVEL:EXPLORE", "Sprache & Fotospots", "Griechisch und Fotomotive")
)

/**
 * Guide-Startseite: Suche, praktische Infos und Tipp-Kategorien als Kacheln.
 * [onOpen] bekommt den Seitenschlüssel: "SEARCH", "ALL", "SAVED", "CAT:<Kategorie>" oder "TRAVEL:<Bereich>".
 */
@Composable
fun GuideHub(
    padding: PaddingValues,
    scrollToTopRequest: Int,
    onOpen: (String) -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    LaunchedEffect(scrollToTopRequest) {
        if (scrollToTopRequest > 0) listState.animateScrollToItem(0)
    }
    val savedCount = CompassPreferences.saved(context).size
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().testTag("guide-hub").padding(padding),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Guide", style = PostcardType.Title, color = KosInk)
                PostcardLabel("Tipps und Infos für Kos")
            }
        }
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .clip(PostcardShape.Panel)
                    .background(HomeCardColor)
                    .border(1.dp, HomeCardBorder, PostcardShape.Panel)
                    .clickable { onOpen("SEARCH") }
                    .padding(horizontal = 16.dp)
                    .testTag("guide-search"),
                contentAlignment = Alignment.CenterStart
            ) {
                Text("Tipps durchsuchen", style = PostcardType.Body, color = KosInk.copy(alpha = 0.65f))
            }
        }
        item { PostcardLabel("Praktisch") }
        items(practicalTiles.chunked(2)) { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (key, title, subtitle) ->
                    GuideInfoTile(
                        title, subtitle,
                        onClick = { onOpen(key) },
                        modifier = Modifier.weight(1f).testTag("travel-area-${key.substringAfter(':').lowercase()}"),
                        emphasized = key == "TRAVEL:HELP"
                    )
                }
            }
        }
        item { PostcardLabel("Entdecken") }
        items(compassCategories.chunked(2)) { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { category ->
                    val count = compassTips.count { it.category == category.title }
                    GuidePhotoTile(
                        title = category.title,
                        caption = if (count == 1) "1 Tipp" else "$count Tipps",
                        imageRes = category.imageRes,
                        onClick = { onOpen("CAT:${category.title}") },
                        modifier = Modifier.weight(1f).testTag("guide-category-${category.title}")
                    )
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GuideInfoTile(
                    "Alle Tipps", "${compassTips.size} ausgewählte Orte",
                    onClick = { onOpen("ALL") },
                    modifier = Modifier.weight(1f).testTag("guide-all")
                )
                GuideInfoTile(
                    "Gemerkt", if (savedCount == 0) "Noch nichts gemerkt" else "$savedCount Tipps",
                    onClick = { onOpen("SAVED") },
                    modifier = Modifier.weight(1f).testTag("guide-saved")
                )
            }
        }
    }
}

@Composable
private fun GuideInfoTile(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false
) {
    Column(
        modifier = modifier
            .heightIn(min = 96.dp)
            .clip(PostcardShape.Panel)
            .background(if (emphasized) HomeAccent.copy(alpha = 0.18f) else HomeCardColor)
            .border(1.dp, if (emphasized) HomeAccent.copy(alpha = 0.85f) else HomeCardBorder, PostcardShape.Panel)
            .clickable(onClick = onClick)
            .padding(PostcardSpace.Panel),
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = PostcardType.Heading, color = KosInk)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = PostcardType.Body, color = KosInk.copy(alpha = 0.8f))
    }
}

@Composable
private fun GuidePhotoTile(
    title: String,
    caption: String,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(128.dp)
            .clip(PostcardShape.Panel)
            .clickable(onClick = onClick)
    ) {
        Image(painterResource(imageRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
        Box(
            Modifier.matchParentSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))))
        )
        Column(Modifier.align(Alignment.BottomStart).padding(12.dp)) {
            Text(title, style = PostcardType.Heading, color = Color.White)
            Text(caption, style = PostcardType.Label, color = Color.White.copy(alpha = 0.85f))
        }
    }
}
