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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CompassScreen(
    padding: PaddingValues,
    scrollToTopRequest: Int = 0,
    onOpenCommunity: () -> Unit,
    onDetailVisibilityChanged: (Boolean) -> Unit = {}
) {
    val gridState = rememberLazyGridState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(scrollToTopRequest) {
        if (scrollToTopRequest > 0) gridState.animateScrollToItem(0)
    }
    val categories = remember {
        listOf(
            CompassCategory("Tigaki", "Tipps direkt rund um euren Urlaubsort", R.drawable.kos_rot_23),
            CompassCategory("Essen", "Griechische Klassiker probieren", R.drawable.category_greek_food),
            CompassCategory("Strände", "Tigaki, Marmari und ruhige Badetage", R.drawable.excursion_marmari),
            CompassCategory("Ausflüge", "Orte, Besuchszeiten und Kombinationen", R.drawable.kos_rot_11),
            CompassCategory("Mobilität", "Bus, Mietwagen und sichere Rückfahrten", R.drawable.kos_rot_19),
            CompassCategory("Unterkünfte", "Euer Hotel und weitere Erfahrungen", R.drawable.kos_rot_25)
        )
    }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedTipId by rememberSaveable { mutableStateOf<String?>(null) }
    var categorySelectionRequest by rememberSaveable { mutableStateOf(0) }
    var showFilter by rememberSaveable { mutableStateOf(false) }
    val persistedTips = remember(context) {
        CompassPreferences.migrateLegacyTitles(context, compassTips)
        CompassPreferences.saved(context) to CompassPreferences.visited(context)
    }
    var savedTips by remember(context) { mutableStateOf(persistedTips.first) }
    var visitedTips by remember(context) { mutableStateOf(persistedTips.second) }
    val visible = remember(selectedCategory, searchQuery, selectedFilter) {
        filterCompassTips(compassTips, searchQuery, selectedCategory).filter { tip ->
            selectedFilter == null || matchesCompassQuickFilter(tip, selectedFilter.orEmpty())
        }
    }
    val dayPlan = remember(savedTips) { buildCompassDayPlan(compassTips, savedTips) }
    val selectedTip = remember(selectedTipId) { compassTips.firstOrNull { it.id == selectedTipId } }
    val isOverview = selectedCategory == null && searchQuery.isBlank() && selectedFilter == null

    fun openTip(id: String) {
        onDetailVisibilityChanged(true)
        selectedTipId = id
    }

    fun closeTip() {
        selectedTipId = null
        onDetailVisibilityChanged(false)
    }

    LaunchedEffect(selectedTipId) {
        onDetailVisibilityChanged(selectedTipId != null)
    }
    LaunchedEffect(categorySelectionRequest) {
        if (categorySelectionRequest > 0) gridState.animateScrollToItem(0)
    }
    DisposableEffect(Unit) {
        onDispose { onDetailVisibilityChanged(false) }
    }
    BackHandler(
        enabled = selectedTipId != null || selectedCategory != null ||
            searchQuery.isNotBlank() || selectedFilter != null
    ) {
        if (selectedTipId != null) {
            closeTip()
        } else {
            selectedCategory = null
            searchQuery = ""
            selectedFilter = null
            categorySelectionRequest += 1
        }
    }
    fun toggleSaved(id: String) {
        savedTips = CompassPreferences.toggleSaved(context, id)
    }

    fun toggleVisited(id: String) {
        visitedTips = CompassPreferences.toggleVisited(context, id)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize().testTag("compass-screen"),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val full: LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(maxLineSpan) }
            item(key = "header", span = full) {
                CompassOverviewHeader(count = compassTips.size, onRandomTip = { openTip(compassTips.random().id) })
            }
            item(key = "search", span = full) {
                CompassSearchField(query = searchQuery, onQueryChange = { searchQuery = it })
            }
            item(key = "chips", span = full) {
                CompassChipRow(
                    categories = categories,
                    selected = selectedCategory,
                    onSelected = { selectedCategory = it },
                    filterActive = selectedFilter != null,
                    onOpenFilter = { showFilter = true }
                )
            }
            if (isOverview && dayPlan.isNotEmpty()) {
                item(key = "plan", span = full) { CompassDayPlanCard(dayPlan) }
            }
            if (!isOverview) {
                item(key = "count", span = full) {
                    Text("${visible.size} Treffer", color = KosInk.copy(alpha = 0.75f), fontSize = 12.sp, fontFamily = KosSans)
                }
            }
            items(visible, key = { it.id }) { tip ->
                CompassTipPhotoCard(
                    tip = tip,
                    isSaved = tip.id in savedTips,
                    isVisited = tip.id in visitedTips,
                    onOpen = { openTip(tip.id) },
                    onToggleSaved = { toggleSaved(tip.id) }
                )
            }
            if (isOverview) {
                item(key = "community", span = full) {
                    CompassCommunityCard(modifier = Modifier.fillMaxWidth(), onClick = onOpenCommunity)
                }
            } else {
                item(key = "disclaimer", span = full) {
                    Text(
                        "Persönliche Erfahrungen können sich ändern. Preise, Öffnungszeiten und Bedingungen bitte vor Ort prüfen.",
                        color = KosInk.copy(alpha = 0.7f), fontSize = 11.sp, lineHeight = 15.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
        if (showFilter) {
            CompassFilterSheet(
                selected = selectedFilter,
                onSelected = { selectedFilter = it },
                onDismiss = { showFilter = false }
            )
        }

        selectedTip?.let { tip ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(KosPanel, KosBg)))
                    .testTag("compass-tip-overlay")
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 76.dp,
                        end = 16.dp,
                        bottom = 32.dp
                    )
                ) {
                    item {
                        CompassTipCard(
                            tip = tip,
                            isSaved = tip.id in savedTips,
                            isVisited = tip.id in visitedTips,
                            expanded = true,
                            showCloseButton = false,
                            onToggleExpanded = ::closeTip,
                            onClose = ::closeTip,
                            onToggleSaved = { toggleSaved(tip.id) },
                            onToggleVisited = { toggleVisited(tip.id) },
                            onOpenMap = {
                                tip.mapsUrl?.let(uriHandler::openUri)
                            },
                            onOpenSource = { tip.sourceUrl?.let(uriHandler::openUri) }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFA101719))
                        .padding(start = 20.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${tip.category.uppercase()} · TIPP",
                        color = KosInk,
                        fontSize = 12.sp,
                        fontFamily = KosSans
                    )
                    TextButton(
                        onClick = ::closeTip,
                        modifier = Modifier
                            .testTag("compass-close-${tip.id}")
                            .semantics {
                                contentDescription = "Tipp schließen und zur Tipps-Übersicht zurückkehren"
                            }
                    ) {
                        Text("×", color = KosInk, fontSize = 28.sp)
                    }
                }
            }
        }
    }
}
