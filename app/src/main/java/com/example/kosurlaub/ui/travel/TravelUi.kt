package com.example.kosurlaub.ui.travel
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.speech.tts.TextToSpeech
import java.util.Locale
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class TravelArea {
    TODAY,
    MOBILITY,
    EXPLORE,
    HELP
}

@Composable
fun TravelScreen(
    padding: PaddingValues,
    scrollToTopRequest: Int = 0,
    area: String = "TODAY",
    onBack: () -> Unit,
    onDetailVisibilityChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    LaunchedEffect(scrollToTopRequest) {
        if (scrollToTopRequest > 0) listState.animateScrollToItem(0)
    }
    var marineWeather by remember(context) {
        mutableStateOf(MarineWeatherRepository.cached(context))
    }
    var isMarineLoading by remember { mutableStateOf(false) }
    var marineRefreshFailed by remember { mutableStateOf(false) }
    var favorites by remember(context) {
        mutableStateOf(TravelPreferences.favorites(context))
    }
    val selectedArea = TravelArea.entries.firstOrNull { it.name == area } ?: TravelArea.TODAY
    var showMobilitySources by rememberSaveable { mutableStateOf(false) }
    var showFerrySources by rememberSaveable { mutableStateOf(false) }
    var showMoreHelp by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) { onDetailVisibilityChanged(false) }

    val weather = WeatherRepository.cached(context)
    val openSource: (String) -> Unit = { url ->
        if (isTrustedTravelUrl(url)) runCatching { uriHandler.openUri(url) }
    }
    val openMap: (String) -> Unit = { query ->
        val uri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
    }
    val callNumber: (String) -> Unit = { number ->
        runCatching {
            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
        }
    }
    val refreshMarine: () -> Unit = {
        if (!isMarineLoading) {
            scope.launch {
                isMarineLoading = true
                marineRefreshFailed = false
                val fresh = withContext(Dispatchers.IO) {
                    MarineWeatherRepository.fetch()?.also {
                        MarineWeatherRepository.save(context.applicationContext, it)
                    }
                }
                if (fresh != null) marineWeather = fresh else marineRefreshFailed = true
                isMarineLoading = false
            }
        }
    }
    LaunchedEffect(Unit) {
        val ageMillis = System.currentTimeMillis() - (marineWeather?.fetchedAtMillis ?: 0L)
        if (marineWeather == null || ageMillis > 3 * 60 * 60 * 1000L) refreshMarine()
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .testTag("travel-area-screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TravelAreaDetailHeader(selectedArea, onBack)
        }
        when (selectedArea) {
            TravelArea.TODAY -> {
                item { TripInfoCard(onOpenMap = openMap) }
                item {
                    MarineWeatherCard(
                        marineWeather = marineWeather,
                        weather = weather,
                        isLoading = isMarineLoading,
                        refreshFailed = marineRefreshFailed,
                        onRefresh = refreshMarine
                    )
                }
            }
            TravelArea.MOBILITY -> {
                item { BusCard(onOpenSite = { openSource(KtelKosTimetable.OFFICIAL_URL) }) }
                item { BusOverviewCard() }
                item {
                    ExpandableTravelGroup(
                        titleRes = R.string.travel_more_mobility_sources,
                        testTag = "travel-more-mobility",
                        expanded = showMobilitySources,
                        onToggle = { showMobilitySources = !showMobilitySources }
                    )
                }
                if (showMobilitySources) {
                    items(travelSources, key = { "mobility_${it.url}" }) { source ->
                        TravelSourceCard(source = source, onOpen = { openSource(source.url) })
                    }
                    item { SupportingTravelText(R.string.travel_sources_hint) }
                }
            }
            TravelArea.EXPLORE -> {
                item { GreekPhrasebookCard() }
                item { PhotoSpotsCard(onOpenMap = openMap) }
                items(excursionIdeas, key = { it.id }) { idea ->
                    ExcursionCard(
                        idea = idea,
                        isFavorite = idea.id in favorites,
                        onToggleFavorite = {
                            favorites = TravelPreferences.toggleFavorite(context, idea.id)
                        },
                        onMap = { openMap(idea.mapQuery) },
                        onOpenImageSource = {
                            runCatching { uriHandler.openUri(idea.imageUrl) }
                        },
                        onOpen = { openSource(idea.url) }
                    )
                }
                item {
                    ExpandableTravelGroup(
                        titleRes = R.string.travel_more_ferry_sources,
                        testTag = "travel-more-explore",
                        expanded = showFerrySources,
                        onToggle = { showFerrySources = !showFerrySources }
                    )
                }
                if (showFerrySources) {
                    items(ferryAndEventSources, key = { "explore_${it.url}" }) { source ->
                        TravelSourceCard(source = source, onOpen = { openSource(source.url) })
                    }
                    item { SupportingTravelText(R.string.travel_offline_hint) }
                }
            }
            TravelArea.HELP -> {
                item { EmergencyCard(onCall = { callNumber("112") }) }
                item {
                    TravelMapHelpCard(
                        onHospital = { openMap("General Hospital of Kos") },
                        onPharmacy = { openMap("pharmacy near me") }
                    )
                }
                item { KosEmergencyCard(onCall = callNumber, onOpenMap = openMap) }
                item {
                    ExpandableTravelGroup(
                        titleRes = R.string.travel_more_help,
                        testTag = "travel-more-help",
                        expanded = showMoreHelp,
                        onToggle = { showMoreHelp = !showMoreHelp }
                    )
                }
                if (showMoreHelp) {
                    item { EmergencyContactsCard(emergencyContacts, callNumber) }
                    item { SupportingTravelText(R.string.travel_emergency_disclaimer) }
                }
            }
        }
    }
}

@Composable
private fun TravelOverviewScreen(
    padding: PaddingValues,
    listState: LazyListState,
    onOpenArea: (TravelArea) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .testTag("travel-screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { TravelHeader() }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TravelOverviewCard(
                        titleRes = R.string.travel_area_today,
                        descriptionRes = R.string.travel_area_today_description,
                        testTag = "travel-area-today",
                        onClick = { onOpenArea(TravelArea.TODAY) },
                        modifier = Modifier.weight(1f)
                    )
                    TravelOverviewCard(
                        titleRes = R.string.travel_area_mobility,
                        descriptionRes = R.string.travel_area_mobility_description,
                        testTag = "travel-area-mobility",
                        onClick = { onOpenArea(TravelArea.MOBILITY) },
                        modifier = Modifier.weight(1f),
                        emphasized = true
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TravelOverviewCard(
                        titleRes = R.string.travel_area_explore,
                        descriptionRes = R.string.travel_area_explore_description,
                        testTag = "travel-area-explore",
                        onClick = { onOpenArea(TravelArea.EXPLORE) },
                        modifier = Modifier.weight(1f)
                    )
                    TravelOverviewCard(
                        titleRes = R.string.travel_area_help,
                        descriptionRes = R.string.travel_area_help_description,
                        testTag = "travel-area-help",
                        onClick = { onOpenArea(TravelArea.HELP) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TravelOverviewCard(
    titleRes: Int,
    descriptionRes: Int,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false
) {
    Column(
        modifier = modifier
            .heightIn(min = 132.dp)
            .background(
                if (emphasized) HomeAccent.copy(alpha = 0.22f) else HomeCardColor,
                HomeCardShape
            )
            .border(
                1.dp,
                if (emphasized) HomeAccent.copy(alpha = 0.85f) else HomeCardBorder,
                HomeCardShape
            )
            .testTag(testTag)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(titleRes),
            color = KosInk,
            fontSize = PostcardMetrics.BODY_SP.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSans
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(descriptionRes),
            color = Color(0xC7FFFFFF),
            fontSize = PostcardMetrics.LABEL_SP.sp,
            lineHeight = 15.sp,
            fontFamily = KosSans
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.travel_area_open),
            color = HomeAccent,
            fontSize = PostcardMetrics.LABEL_SP.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = KosSans
        )
    }
}

@Composable
private fun TravelAreaDetailHeader(area: TravelArea, onBack: () -> Unit) {
    val titleRes = when (area) {
        TravelArea.TODAY -> R.string.travel_area_today
        TravelArea.MOBILITY -> R.string.travel_area_mobility
        TravelArea.EXPLORE -> R.string.travel_area_explore
        TravelArea.HELP -> R.string.travel_area_help
    }
    Column {
        TextButton(onClick = onBack, modifier = Modifier.testTag("travel-area-back")) {
            Text(stringResource(R.string.travel_back), color = HomeAccent)
        }
        Text(
            text = stringResource(titleRes),
            color = KosInk,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSerif
        )
    }
}

@Composable
private fun TravelHeader() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            PostcardLabel(stringResource(R.string.travel_screen_label))
        }
        Text(
            text = stringResource(R.string.travel_screen_title),
            color = KosInk,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSerif,
            modifier = Modifier.testTag("travel-header-title")
        )
        Text(
            text = stringResource(R.string.travel_screen_intro),
            color = KosInk.copy(alpha = 0.75f),
            fontSize = PostcardMetrics.BODY_SP.sp,
            lineHeight = 18.sp,
            fontFamily = KosSans
        )
    }
}

@Composable
private fun SectionLabel(textRes: Int) {
    Text(
        text = stringResource(textRes),
        color = HomeAccent,
        fontSize = PostcardMetrics.LABEL_SP.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
    )
}

@Composable
private fun ExpandableTravelGroup(
    titleRes: Int,
    testTag: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    TextButton(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth().testTag(testTag)
    ) {
        Text(
            text = stringResource(titleRes),
            color = KosInk.copy(alpha = 0.8f),
            fontFamily = KosSans,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (expanded) "−" else "+",
            color = HomeAccent,
            fontSize = 20.sp
        )
    }
}

@Composable
private fun SupportingTravelText(textRes: Int) {
    Text(
        text = stringResource(textRes),
        color = KosInk.copy(alpha = 0.6f),
        fontSize = PostcardMetrics.LABEL_SP.sp,
        lineHeight = 15.sp,
        fontFamily = KosSans,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )
}

@Composable
private fun BusOverviewCard() {
    val destinations = listOf(
        R.string.travel_bus_kos,
        R.string.travel_bus_antimachia,
        R.string.travel_bus_marmari,
        R.string.travel_bus_seven_springs,
        R.string.travel_bus_embros
    )
    TravelCardContainer {
        Text(
            text = stringResource(R.string.travel_bus_intro),
            color = KosInk,
            fontSize = PostcardMetrics.BODY_SP.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = KosSans
        )
        Spacer(Modifier.height(8.dp))
        destinations.forEach { destination ->
            Text(
                text = "• ${stringResource(destination)}",
                color = KosInk.copy(alpha = 0.9f),
                fontSize = PostcardMetrics.BODY_SP.sp,
                lineHeight = 20.sp,
                fontFamily = KosSans
            )
        }
    }
}

@Composable
private fun TravelSourceCard(source: TravelSource, onOpen: () -> Unit) {
    TravelCardContainer {
        Text(
            text = stringResource(source.titleRes),
            color = KosInk,
            fontSize = PostcardMetrics.HEADING_SP.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = KosSerif
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(source.descriptionRes),
            color = KosInk.copy(alpha = 0.85f),
            fontSize = PostcardMetrics.BODY_SP.sp,
            lineHeight = 18.sp,
            fontFamily = KosSans
        )
        TextButton(onClick = onOpen, contentPadding = PaddingValues(top = 6.dp)) {
            Text(
                text = stringResource(R.string.travel_open_source),
                color = HomeAccent,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ExcursionCard(
    idea: ExcursionIdea,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onMap: () -> Unit,
    onOpenImageSource: () -> Unit,
    onOpen: () -> Unit
) {
    TravelCardContainer {
        Image(
            painter = painterResource(idea.imageRes),
            contentDescription = stringResource(idea.titleRes),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
                .clip(HomeCardShape)
        )
        Text(
            text = idea.imageCredit,
            color = KosInk.copy(alpha = 0.6f),
            fontSize = PostcardMetrics.LABEL_SP.sp,
            fontFamily = KosSans,
            modifier = Modifier
                .align(Alignment.End)
                .clickable(onClick = onOpenImageSource)
                .padding(horizontal = 4.dp, vertical = 5.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(idea.titleRes),
            color = KosInk,
            fontSize = PostcardMetrics.HEADING_SP.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = KosSerif
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = stringResource(idea.metaRes),
            color = HomeAccent,
            fontSize = PostcardMetrics.LABEL_SP.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = KosSans
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = stringResource(idea.descriptionRes),
            color = KosInk.copy(alpha = 0.9f),
            fontSize = PostcardMetrics.BODY_SP.sp,
            lineHeight = 18.sp,
            fontFamily = KosSans
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onToggleFavorite) {
                Text(
                    text = stringResource(
                        if (isFavorite) R.string.travel_favorite_remove
                        else R.string.travel_favorite_add
                    ),
                    color = if (isFavorite) KosInk else HomeAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onMap) {
                Text(
                    text = stringResource(R.string.travel_map_action),
                    color = HomeAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        TextButton(onClick = onOpen, contentPadding = PaddingValues(top = 2.dp)) {
            Text(
                text = stringResource(R.string.travel_more_information),
                color = HomeAccent,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmergencyCard(onCall: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PostcardShape.Panel)
            .background(KosStamp)
            .padding(16.dp)
            .testTag("sos-112-card")
    ) {
        Text(
            text = stringResource(R.string.travel_emergency_title).uppercase(),
            style = PostcardType.Label,
            color = Color.White.copy(alpha = 0.9f)
        )
        Text("112", style = PostcardType.Numeral.copy(fontSize = 64.sp), color = Color.White)
        Text(
            text = stringResource(R.string.travel_emergency_description),
            style = PostcardType.Body,
            color = Color.White
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onCall,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = KosStamp)
        ) {
            Text(
                text = stringResource(R.string.travel_emergency_action),
                style = PostcardType.Heading
            )
        }
    }
}

@Composable
internal fun TravelCardContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HomeCardColor, HomeCardShape)
            .border(1.dp, HomeCardBorder, HomeCardShape)
            .padding(16.dp),
        content = content
    )
}

@Composable
private fun rememberGreekTts(): (String) -> Unit {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val localeResult = instance?.setLanguage(Locale.forLanguageTag("el-GR"))
                if (localeResult == TextToSpeech.LANG_MISSING_DATA || localeResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    instance?.setLanguage(Locale.forLanguageTag("el"))
                }
            }
        }
        tts = instance
        onDispose {
            instance?.stop()
            instance?.shutdown()
        }
    }
    return remember(tts) {
        { text: String ->
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "greek_tts_${text.hashCode()}")
        }
    }
}

@Composable
private fun GreekPhrasebookCard() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val speakGreek = rememberGreekTts()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeCardColor)
            .border(1.dp, HomeAccent.copy(alpha = 0.5f), HomeCardShape)
            .padding(16.dp)
            .testTag("greek-phrasebook-card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                PostcardLabel("GRIECHISCH FÜR DIE TAVERNE")
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Sprachführer & Audio-Aussprache",
                    color = KosInk,
                    fontSize = PostcardMetrics.BODY_SP.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = KosSans
                )
            }
            Text(
                text = if (expanded) "▲" else "▼",
                color = HomeAccent,
                fontSize = PostcardMetrics.BODY_SP.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (expanded) {
            Spacer(Modifier.height(12.dp))
            greekPhrases.forEach { phrase ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { speakGreek(phrase.ttsText) }
                        .padding(vertical = 6.dp, horizontal = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(
                                text = phrase.greek,
                                color = KosInk,
                                fontSize = PostcardMetrics.BODY_SP.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = KosSans
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "",
                                fontSize = PostcardMetrics.BODY_SP.sp
                            )
                        }
                        Text(
                            text = phrase.german,
                            color = HomeAccent,
                            fontSize = PostcardMetrics.BODY_SP.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = KosSans
                        )
                    }
                    Text(
                        text = "Aussprache: ${phrase.phonetic}",
                        color = KosInk.copy(alpha = 0.6f),
                        fontSize = PostcardMetrics.LABEL_SP.sp,
                        fontFamily = KosSans
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoSpotsCard(onOpenMap: (String) -> Unit) {
    var selectedCategory by rememberSaveable { mutableStateOf("Alle") }
    val categories = listOf("Alle", "Sonnenaufgang", "Vormittag", "Golden Hour", "Natur")

    val filteredSpots = remember(selectedCategory) {
        if (selectedCategory == "Alle") photoSpots
        else photoSpots.filter { it.timeCategory == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeCardColor)
            .border(1.dp, HomeAccent.copy(alpha = 0.5f), HomeCardShape)
            .padding(16.dp)
            .testTag("photo-spots-card")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PostcardLabel("DIE 10 BESTEN FOTOSPOTS UM TIGAKI")
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Perfekte Uhrzeiten & Fototipps für Urlaubsfotos",
            color = KosInk,
            fontSize = PostcardMetrics.BODY_SP.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSans
        )
        Spacer(Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                PostcardChip(category, isSelected, { selectedCategory = category })
            }
        }

        Spacer(Modifier.height(12.dp))

        filteredSpots.forEach { spot ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(PostcardShape.Tile)
                    .background(Color(0x20000000))
                    .border(1.dp, KosInk.copy(alpha = 0.2f), PostcardShape.Tile)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = spot.title,
                        color = KosInk,
                        fontSize = PostcardMetrics.BODY_SP.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = KosSans,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { onOpenMap(spot.mapQuery) }, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("Karte", color = HomeAccent, fontSize = PostcardMetrics.LABEL_SP.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text(
                    text = "${spot.bestTime} · ${spot.location}",
                    color = HomeAccent,
                    fontSize = PostcardMetrics.LABEL_SP.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                )
                Text(
                    text = spot.tip,
                    color = Color(0xDDFFFFFF),
                    fontSize = PostcardMetrics.BODY_SP.sp,
                    lineHeight = 17.sp,
                    fontFamily = KosSans
                )
            }
        }
    }
}

@Composable
private fun KosEmergencyCard(onCall: (String) -> Unit, onOpenMap: (String) -> Unit) {
    PaperPanel(modifier = Modifier.testTag("kos-emergency-sos-card")) {
        PostcardLabel("Weitere Nummern in Griechenland")
        SosNumberRow("Polizei", "100", onCall)
        SosNumberRow("Rettungsdienst", "166", onCall)
        SosNumberRow("Feuerwehr", "199", onCall)
        SosNumberRow(
            "Hotel-Rezeption", TripData.HOTEL_PHONE, onCall,
            note = "Nummer nicht bestätigt",
            onOpenMap = { onOpenMap("${TripData.HOTEL_LAT},${TripData.HOTEL_LON}(${TripData.HOTEL_NAME})") }
        )
    }
}

@Composable
private fun SosNumberRow(
    label: String,
    number: String,
    onCall: (String) -> Unit,
    note: String? = null,
    onOpenMap: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCall(number) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = PostcardType.Body, color = KosInk)
            if (note != null) Text(note.uppercase(), style = PostcardType.Label, color = KosInk.copy(alpha = 0.7f))
        }
        if (onOpenMap != null) {
            TextButton(onClick = onOpenMap) { Text("Karte", style = PostcardType.Label, color = KosInk) }
        }
        Text(
            number,
            style = if (number.length <= 4) PostcardType.Numeral else PostcardType.Numeral.copy(fontSize = PostcardType.Heading.fontSize),
            color = KosInk
        )
    }
}
