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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import java.util.Calendar


@Composable
fun CompassScreen(
    padding: PaddingValues,
    scrollToTopRequest: Int = 0,
    onOpenCommunity: () -> Unit,
    onDetailVisibilityChanged: (Boolean) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(scrollToTopRequest) {
        if (scrollToTopRequest > 0) listState.animateScrollToItem(0)
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
    val featuredTip = remember { featuredCompassTip(compassTips, Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) }
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
        if (categorySelectionRequest > 0) listState.animateScrollToItem(0)
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

    fun selectCategory(category: String) {
        selectedCategory = category
        categorySelectionRequest += 1
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("compass-screen"),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "header") {
                CompassOverviewHeader(
                    count = compassTips.size,
                    onRandomTip = { openTip(compassTips.random().id) }
                )
            }
            if (isOverview) {
                item(key = "hero") {
                    CompassHeroCard(
                        tip = featuredTip,
                        isSaved = featuredTip.id in savedTips,
                        onToggleSaved = { toggleSaved(featuredTip.id) },
                        onOpen = { openTip(featuredTip.id) }
                    )
                }
            }
            item(key = "search") {
                CompassSearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }
            item(key = "categories") {
                CompassCategoryFilters(
                    categories = categories,
                    selected = selectedCategory,
                    onSelected = { selectedCategory = it }
                )
            }
            item {
                CompassQuickFilters(
                    selected = selectedFilter,
                    onSelected = { filter ->
                        selectedFilter = if (selectedFilter == filter) null else filter
                    }
                )
            }
            if (isOverview && dayPlan.isNotEmpty()) {
                item { CompassDayPlanCard(dayPlan) }
            }
            if (isOverview) {
                items(categories.chunked(2), key = { row -> row.first().title }) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { category ->
                            CompassCategoryCard(
                                category = category,
                                count = compassTips.count { it.category == category.title },
                                modifier = Modifier.weight(1f),
                                onClick = { selectCategory(category.title) }
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                item(key = "community") {
                    CompassCommunityCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onOpenCommunity
                    )
                }
            } else {
                item {
                    Text(
                        "${visible.size} Treffer",
                        color = KosInk.copy(alpha = 0.75f),
                        fontSize = 12.sp,
                        fontFamily = KosSans
                    )
                }
                items(visible, key = { it.id }) { tip ->
                    CompassTipCard(
                        tip = tip,
                        isSaved = tip.id in savedTips,
                        isVisited = tip.id in visitedTips,
                        expanded = false,
                        onToggleExpanded = { openTip(tip.id) },
                        onClose = ::closeTip,
                        onToggleSaved = { toggleSaved(tip.id) },
                        onToggleVisited = { toggleVisited(tip.id) },
                        onOpenMap = {
                            tip.mapsUrl?.let(uriHandler::openUri)
                        },
                        onOpenSource = { tip.sourceUrl?.let(uriHandler::openUri) }
                    )
                }
                item {
                    Text(
                        "Persönliche Erfahrungen können sich ändern. Preise, Öffnungszeiten und Bedingungen bitte vor Ort prüfen.",
                        color = KosInk.copy(alpha = 0.5f), fontSize = 10.sp, lineHeight = 15.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
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
