package com.example.kosurlaub

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.home.today.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import androidx.compose.foundation.border
import com.example.kosurlaub.widget.*

import android.content.Intent
import android.widget.Toast
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.ui.theme.KosWidgetTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

// TODO: Keine spezifische Facebook-Gruppe fuer Kos bekannt - zeigt vorerst eine Suche.
private const val COMMUNITY_URL = "https://www.facebook.com/search/groups/?q=kos%20urlaub"

class MainActivity : ComponentActivity() {
    companion object {
        const val EXTRA_OPEN_TRAVEL = "open_travel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppClock.init(applicationContext)
        KosWidgetWorker.ensureScheduled(applicationContext)
        setContent {
            val themeMode = remember { mutableStateOf(ThemeMode.load(applicationContext)) }
            val dark = when (themeMode.value) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            LaunchedEffect(dark) {
                val bars = if (dark) {
                    SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = bars, navigationBarStyle = bars)
            }
            KosWidgetTheme(themeMode.value) {
                KosApp(
                    startInTravel = intent.getBooleanExtra(EXTRA_OPEN_TRAVEL, false),
                    themeMode = themeMode.value,
                    onThemeMode = {
                        ThemeMode.save(applicationContext, it)
                        themeMode.value = it
                    }
                )
            }
        }
    }
}

private enum class MainDestination { TODAY, DISCOVER, DIARY }

@Composable
private fun KosApp(
    startInTravel: Boolean = false,
    themeMode: ThemeMode,
    onThemeMode: (ThemeMode) -> Unit
) {
    val context = LocalContext.current
    val destinationName = rememberSaveable {
        mutableStateOf(if (startInTravel) MainDestination.DISCOVER.name else MainDestination.TODAY.name)
    }
    val discoverTab = rememberSaveable { mutableStateOf(if (startInTravel) "TRAVEL" else "TIPS") }
    val nestedDetailVisible = remember { mutableStateOf(false) }
    val scrollToTopRequest = remember { mutableIntStateOf(0) }
    val areaRequest = remember { mutableIntStateOf(0) }
    val travelArea = remember { mutableStateOf("HELP") }
    val selectedDay = rememberSaveable { mutableIntStateOf(0) }
    val todayIndex = remember { TripDays.today(AppClock.nowMillis())?.index }
    val destination = MainDestination.valueOf(destinationName.value)

    BackHandler(enabled = !nestedDetailVisible.value && destination != MainDestination.TODAY) {
        destinationName.value = MainDestination.TODAY.name
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(KosBg),
        containerColor = Color.Transparent,
        bottomBar = {
            if (!nestedDetailVisible.value) {
                Column {
                    if (destination == MainDestination.TODAY) {
                        DayStrip(
                            days = TripDays.all,
                            todayIndex = todayIndex,
                            selected = selectedDay.intValue.takeIf { it > 0 },
                            onSelect = { day ->
                                selectedDay.intValue = if (selectedDay.intValue == day.index) 0 else day.index
                            }
                        )
                    }
                    MainNavigationBar(
                        selected = destination,
                        onSelect = { selected ->
                            if (selected == destination) scrollToTopRequest.intValue++
                            destinationName.value = selected.name
                        }
                    )
                }
            }
        }
    ) { padding ->
        when (destination) {
            MainDestination.TODAY -> KosHome(
                padding = padding,
                selectedDay = TripDays.all.firstOrNull { it.index == selectedDay.intValue },
                onOpenIdeas = { discoverTab.value = "TIPS"; destinationName.value = MainDestination.DISCOVER.name },
                themeMode = themeMode,
                onThemeMode = onThemeMode,
                scrollToTopRequest = scrollToTopRequest.intValue,
                onOpenTravel = { area ->
                    travelArea.value = area
                    discoverTab.value = "TRAVEL"
                    destinationName.value = MainDestination.DISCOVER.name
                    areaRequest.intValue++
                }
            )
            MainDestination.DISCOVER -> DiscoverScreen(
                padding = padding,
                tab = discoverTab.value,
                onTab = { discoverTab.value = it },
                openAreaRequest = areaRequest.intValue,
                openArea = travelArea.value,
                scrollToTopRequest = scrollToTopRequest.intValue,
                onBack = { destinationName.value = MainDestination.TODAY.name },
                onOpenCommunity = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(COMMUNITY_URL))) },
                onDetailVisibilityChanged = { nestedDetailVisible.value = it }
            )
            MainDestination.DIARY -> DiaryScreen(
                padding = padding,
                scrollToTopRequest = scrollToTopRequest.intValue,
                phase = TripPhase.of(AppClock.nowMillis())
            )
        }
    }
}

@Composable
private fun MainNavigationBar(
    selected: MainDestination,
    onSelect: (MainDestination) -> Unit
) {
    NavigationBar(containerColor = KosNav) {
        MainDestination.entries.forEach { destination ->
            val labelRes = when (destination) {
                MainDestination.TODAY -> R.string.main_nav_today
                MainDestination.DISCOVER -> R.string.main_nav_discover
                MainDestination.DIARY -> R.string.main_nav_diary
            }
            val iconRes = when (destination) {
                MainDestination.TODAY -> R.drawable.ic_nav_home
                MainDestination.DISCOVER -> R.drawable.ic_nav_compass
                MainDestination.DIARY -> R.drawable.ic_nav_diary
            }
            NavigationBarItem(
                selected = destination == selected,
                onClick = { onSelect(destination) },
                icon = { Icon(painter = painterResource(iconRes), contentDescription = null) },
                label = { Text(stringResource(labelRes)) },
                modifier = Modifier.testTag("main-nav-${destination.name.lowercase()}"),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = KosInk,
                    selectedTextColor = KosInk,
                    indicatorColor = KosPanelHigh,
                    unselectedIconColor = KosInk.copy(alpha = 0.7f),
                    unselectedTextColor = KosInk.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
private fun DiscoverScreen(
    padding: PaddingValues,
    tab: String,
    onTab: (String) -> Unit,
    openAreaRequest: Int,
    openArea: String,
    scrollToTopRequest: Int,
    onBack: () -> Unit,
    onOpenCommunity: () -> Unit,
    onDetailVisibilityChanged: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("TIPS" to R.string.discover_tab_tips, "TRAVEL" to R.string.discover_tab_travel).forEach { (key, label) ->
                PostcardChip(
                    label = stringResource(label),
                    selected = tab == key,
                    onClick = { onTab(key) },
                    modifier = Modifier.testTag("discover-tab-${key.lowercase()}")
                )
            }
        }
        val inner = PaddingValues(bottom = padding.calculateBottomPadding())
        Box(modifier = Modifier.weight(1f)) {
            if (tab == "TRAVEL") {
                TravelScreen(
                    padding = inner,
                    openAreaRequest = openAreaRequest,
                    openArea = openArea,
                    scrollToTopRequest = scrollToTopRequest,
                    onBack = onBack,
                    onDetailVisibilityChanged = onDetailVisibilityChanged
                )
            } else {
                CompassScreen(
                    padding = inner,
                    scrollToTopRequest = scrollToTopRequest,
                    onOpenCommunity = onOpenCommunity,
                    onDetailVisibilityChanged = onDetailVisibilityChanged
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KosHome(
    padding: PaddingValues,
    selectedDay: TripDay?,
    onOpenIdeas: () -> Unit,
    themeMode: ThemeMode,
    onThemeMode: (ThemeMode) -> Unit,
    scrollToTopRequest: Int,
    onOpenTravel: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val state = remember { mutableStateOf(HomeState.load(context)) }
    val isRefreshing = remember { mutableStateOf(false) }
    val reloadDone = remember { mutableStateOf(false) }
    val weatherError = remember { mutableStateOf(false) }
    val showSettings = remember { mutableStateOf(false) }
    val showGallery = remember { mutableStateOf(false) }
    val pinnedImage = remember { mutableStateOf(Images.getPinnedImage(context)) }
    val scope = rememberCoroutineScope()
    val updateController = remember(context) { AppUpdateController(context) }
    val scrollState = rememberScrollState()

    LaunchedEffect(updateController) {
        updateController.checkOnStartup()
    }

    LaunchedEffect(scrollToTopRequest) {
        if (scrollToTopRequest > 0) scrollState.animateScrollTo(0)
    }

    LaunchedEffect(reloadDone.value) {
        if (reloadDone.value) { delay(1_500); reloadDone.value = false }
    }

    LaunchedEffect("weather-autoload") {
        if (state.value.weather == null && !isRefreshing.value) {
            isRefreshing.value = true
            val success = fetchAndSaveWeather(context)
            weatherError.value = !success
            if (success) KosCountdownLargeWidgetProvider.updateAllLargeWidgets(context)
            state.value = HomeState.load(context)
            isRefreshing.value = false
            reloadDone.value = success
        }
    }

    LaunchedEffect(Unit) {
        var tick = 0
        while (true) {
            if (tick % 60 == 0) {
                state.value = HomeState.load(context)
            } else {
                val r = CountdownCalculator.calculate()
                state.value = state.value.copy(
                    days = r.days, hours = r.hours,
                    minutes = r.minutes, seconds = r.seconds,
                    isReached = r.isReached, isOnVacation = r.isOnVacation,
                    isOver = r.isOver
                )
            }
            tick++
            delay(1_000)
        }
    }

    val s = state.value
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            updateController.availableUpdate?.let { update ->
                UpdateBanner(
                    update = update,
                    isDownloading = updateController.isWorking,
                    onClick = {
                        if (!updateController.isWorking) {
                            scope.launch {
                                if (updateController.install(update) == UpdateInstallResult.DOWNLOAD_FAILED) {
                                    showUpdateDownloadError(context)
                                }
                            }
                        }
                    }
                )
                Spacer(Modifier.height(16.dp))
            }
            HeaderSection(
                s = s,
                onSettings = { showSettings.value = true }
            )
            Spacer(Modifier.height(18.dp))
            if (selectedDay != null) {
                DayPage(day = selectedDay, onOpenIdeas = onOpenIdeas)
            } else {
            val phase = TripPhase.of(AppClock.nowMillis())
            val privatePath = remember(PrivateImages.version.intValue, s.backgroundRes) {
                Images.privatePathOfTheDay(context)
            }
            val heroPhoto = remember(privatePath) {
                privatePath?.let { PrivateImages.bitmap(context, it, 1200)?.asImageBitmap() }
            }
            if (CountdownCalculator.isFlightDay()) {
                FlightDayTimelineCard()
                Spacer(Modifier.height(16.dp))
            }
            when (phase) {
                TripPhase.BEFORE -> {
                    TodayBefore(s, heroPhoto, onOpenIdeas)
                    Spacer(Modifier.height(16.dp))
                    FreeChecklistCard()
                    Spacer(Modifier.height(16.dp))
                    when (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % 2) {
                        0 -> FactCard(s.factOfTheDay)
                        else -> HighlightCard(kosHighlightOfTheDay())
                    }
                }
                TripPhase.ON_KOS -> {
                    TodayOnKos(s, heroPhoto, onOpenIdeas)
                    Spacer(Modifier.height(16.dp))
                    HomeQuickActions(
                        onOpenTigaki = { onOpenTravel("MOBILITY") },
                        onOpenTavernCalc = { onOpenTravel("EXPLORE") },
                        onOpenPhrasebook = { onOpenTravel("EXPLORE") },
                        onOpenEmergency = { onOpenTravel("HELP") }
                    )
                }
                TripPhase.AFTER -> TodayAfter(
                    s, heroPhoto,
                    entriesCount = TripDays.all.sumOf { DayEntryStore.load(context, it.index).size }
                )
            }
            if (phase != TripPhase.AFTER) {
                Spacer(Modifier.height(12.dp))
                BeachWindRadarCard()
                Spacer(Modifier.height(12.dp))
                BottomSection(
                    s = s,
                    isRefreshing = isRefreshing.value,
                    reloadDone = reloadDone.value,
                    hasError = weatherError.value,
                    onRefresh = {
                        if (!isRefreshing.value) {
                            scope.launch {
                                isRefreshing.value = true
                                val success = fetchAndSaveWeather(context)
                                weatherError.value = !success
                                if (success) KosCountdownLargeWidgetProvider.updateAllLargeWidgets(context)
                                state.value = HomeState.load(context)
                                isRefreshing.value = false
                                reloadDone.value = success
                            }
                        }
                    }
                )
            }
            }
        }
        // Settings-BottomSheet: Teilen + Galerie
        if (showSettings.value) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showSettings.value = false },
                sheetState = sheetState,
                containerColor = KosBg
            ) {
                SettingsSheet(
                    themeMode = themeMode,
                    onThemeMode = onThemeMode,
                    isCheckingUpdate = updateController.isWorking,
                    hasUpdate = updateController.availableUpdate != null,
                    onCheckUpdate = {
                        if (!updateController.isWorking) {
                            scope.launch {
                                when (updateController.checkAndInstall()) {
                                    UpdateInstallResult.INSTALLED -> showSettings.value = false
                                    UpdateInstallResult.DOWNLOAD_FAILED -> showUpdateDownloadError(context)
                                    UpdateInstallResult.NOT_AVAILABLE ->
                                        Toast.makeText(context, "Keine neuere Version gefunden.", Toast.LENGTH_SHORT).show()
                                    UpdateInstallResult.BUSY -> Unit
                                }
                            }
                        }
                    },
                    onShare = {
                        val sv = state.value
                        val msg = buildString {
                            when {
                                sv.isOver -> { append("🌅 Zurück von Kos – es war wunderschön!") }
                                sv.isOnVacation -> { appendLine("🌊 Wir sind auf Kos!"); append("Genießt jeden Augenblick.") }
                                sv.isReached -> { appendLine("🛫 Heute geht's los nach Kos!"); append("Abflug: ${sv.departureDate} um ${sv.departureTime} Uhr") }
                                else -> {
                                    appendLine("🌊 Noch ${sv.days} Tage bis Kos!")
                                    appendLine("Abflug: ${sv.departureDate} um ${sv.departureTime} Uhr")
                                    appendLine()
                                    appendLine("${sv.days} Tage · ${sv.hours.toString().padStart(2,'0')} Std. · ${sv.minutes.toString().padStart(2,'0')} Min.")
                                    appendLine(); append("»${sv.phrase}«")
                                }
                            }
                        }
                        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, msg) }, "Countdown teilen"))
                        showSettings.value = false
                    },
                    onOpenGallery = {
                        showSettings.value = false
                        showGallery.value = true
                    }
                )
            }
        }

        if (showGallery.value) {
            GallerySheet(
                onDismissRequest = { showGallery.value = false },
                onApply = { newImage ->
                    Images.setPinnedImage(context, newImage)
                    pinnedImage.value = newImage
                    KosCountdownLargeWidgetProvider.updateAllLargeWidgets(context)
                    state.value = HomeState.load(context)
                    showGallery.value = false
                },
                currentImageName = Images.rotationImageName(),
                pinnedImageName = pinnedImage.value
            )
        }


        if (updateController.showStartupDialog) {
            updateController.availableUpdate?.let { update ->
                StartupUpdateDialog(
                    update = update,
                    isDownloading = updateController.isWorking,
                    onDismiss = updateController::dismissStartupDialog,
                    onInstall = {
                        if (!updateController.isWorking) {
                            scope.launch {
                                when (updateController.install(update)) {
                                    UpdateInstallResult.INSTALLED -> updateController.dismissStartupDialog()
                                    UpdateInstallResult.DOWNLOAD_FAILED -> showUpdateDownloadError(context)
                                    UpdateInstallResult.NOT_AVAILABLE,
                                    UpdateInstallResult.BUSY -> Unit
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

private fun showUpdateDownloadError(context: android.content.Context) {
    Toast.makeText(context, "Fehler beim Herunterladen des Updates.", Toast.LENGTH_SHORT).show()
}

@Composable
private fun HeaderSection(
    s: HomeState,
    onSettings: () -> Unit
) {
    Row(verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (s.isReached) "Unser Kos-Urlaub" else "Bis zu unserem Kos-Urlaub",
                style = PostcardType.Title,
                color = KosInk
            )
            PostcardLabel("${s.departureDate} · ${s.departureTime} Uhr", Modifier.padding(top = 4.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.offset(x = 12.dp)
        ) {
            // Settings Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .testTag("settings-button")
                    .clickable(onClick = onSettings)
                    .clearAndSetSemantics {
                        contentDescription = "Einstellungen öffnen"
                        onClick {
                            onSettings()
                            true
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(HomeCardColor)
                        .border(1.dp, HomeCardBorder, CircleShape)
                ) {
                    androidx.compose.material3.Icon(
                        painter = androidx.compose.ui.res.painterResource(R.drawable.ic_settings),
                        contentDescription = null,
                        tint = KosInk,
                        modifier = Modifier.align(Alignment.Center).size(20.dp)
                    )
                }
            }
        }
    }
}










/**
 * Begrenzt die System-Schriftgrößenskalierung innerhalb des Blocks auf maxScale.
 * Verhindert, dass die Countdown-Zahlen bei "Riesig"-Einstellung den Screen sprengen.
 */






@Composable
private fun BottomSection(
    s: HomeState,
    isRefreshing: Boolean,
    reloadDone: Boolean,
    hasError: Boolean,
    onRefresh: () -> Unit,
) {
    WeatherCard(s, isRefreshing, reloadDone, hasError, onRefresh)
}

private suspend fun fetchAndSaveWeather(context: android.content.Context): Boolean =
    withContext(Dispatchers.IO) {
        val weather = WeatherRepository.fetch() ?: return@withContext false
        WeatherRepository.save(context, weather)
        true
    }



























private fun kosHighlightOfTheDay(): String {
    val highlights = listOf(
        "Tigaki Beach: Feiner Sand, flaches Wasser — ideal zum Entspannen, direkt vor der Haustür.",
        "Platane des Hippokrates: Der Legende nach unterrichtete der \"Vater der Medizin\" hier seine Schüler.",
        "Asklepieion: Antikes Heilheiligtum mit Blick über die Küste — am besten vormittags besuchen.",
        "Zia bei Sonnenuntergang: Das Bergdorf gilt als bester Aussichtspunkt der Insel.",
        "Burg Nerantzia in Kos-Stadt: Die Johanniterritter-Festung am Hafen — Öffnungszeiten vorab prüfen.",
        "Tzatziki zum Brot: Joghurt, Gurke und Knoblauch — in fast jeder Taverne.",
        "Alikes Salzsee: Kurzer Spaziergang von Tigaki, im Frühjahr und Herbst gut für Vogelbeobachtung.",
        "Fahrrad mieten: Kos ist flach und gut ausgebaut — eine der fahrradfreundlichsten Inseln Griechenlands.",
        "Marmari Beach: Ruhigere Strandalternative zu Tigaki, wenige Autominuten entfernt.",
        "Festung Antimachia: Mittelalterliche Johanniterburg in der Inselmitte.",
        "Therme Embros: Natürlich warme Quelle direkt am Meer bei Agios Fokas.",
        "Bootsausflug nach Nisyros: Die benachbarte Vulkaninsel ist einen Tagesausflug wert.",
        "Melekouni kosten: Sesam-Honig-Riegel — ein griechischer Klassiker zum Mitnehmen.",
        "Abendspaziergang in Kos-Stadt: Hafen, Agora und die Altstadtgassen am Abend erleben.",
        "Tagesausflug nach Bodrum: Mit dem Boot in die Türkei — Reisedokumente vorher prüfen.",
        "Lokalen Markt erkunden: Frische Feigen, Wassermelonen und Tomaten im September.",
        "Weinverkostung: Auch Kos hat eigene Weingüter, die Führungen anbieten.",
        "Casa Romana: Gut erhaltene römische Stadtvilla mit Mosaiken in Kos-Stadt.",
        "Frühmorgens schwimmen: Das Meer vor Tigaki ist am Morgen oft noch ganz ruhig.",
        "Dolmathes probieren: Gefüllte Weinblätter mit Reis und Kräutern — griechische Hausmannskost.",
        "Nachtbaden: Das warme Mittelmeer im September ist abends besonders einladend.",
        "Dikeos-Blick: Der höchste Berg der Insel ist schon von Tigaki aus ein markanter Anblick."
    )
    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    return highlights[dayOfYear % highlights.size]
}






