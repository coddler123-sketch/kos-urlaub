# Kos Urlaub Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Neue private Kos-Urlaubs-App in `C:\Claude_Code_Projects\kos-urlaub` (Paket `com.example.kosurlaub`), ohne Rhodos-Reste und ohne unbelegte Inhalte.

**Architecture:** Fork der alten App `C:\Claude_Code_Projects\kos-countdown-widget` (bleibt unverändert) in den neuen Ordner, danach Umbenennen, Entfernen von News, Updater und dem Rhodos-Fahrplan-System, eine zentrale Reisedaten-Datei `TripData.kt` und neue Kos-Inhalte (Foto-Spots, Bus, SOS, Hotel, Flugdaten). Ein Wächter-Test (`NoRhodesLeftoversTest`) lässt den Build fehlschlagen, sobald ein Rhodos-Begriff im Quelltext steht.

**Tech Stack:** Kotlin 2.2.10, Jetpack Compose (BOM 2026.02.01), AGP 9.2.1, Gradle 9.4.1, minSdk 24, JUnit 4, Open-Meteo.

**Pfade in diesem Plan:** `OLD` = `C:\Claude_Code_Projects\kos-countdown-widget`, `NEW` = `C:\Claude_Code_Projects\kos-urlaub`, `PKG` = `NEW/app/src/main/java/com/example/kosurlaub`, `ADB` = `$LOCALAPPDATA/Android/Sdk/platform-tools/adb.exe`. Alle Shell-Befehle laufen in Git Bash. Git-Schritte sind optional (nur wenn ihr Git nutzen wollt); ohne `git init` einfach überspringen.

**Wichtig für alle Tasks:** Unit-Tests laufen im Ordner `app/` (Arbeitsverzeichnis des Test-JVM). Nach jedem Task: `./gradlew.bat :app:testDebugUnitTest :app:assembleDebug -q` muss ohne Ausgabe enden (grün).

---

## Dateistruktur (Ziel)

| Datei | Aufgabe |
|---|---|
| `app/src/main/java/.../TripData.kt` | Einzige Quelle für Flüge, Gepäck, Hotel, Koordinaten, Zeitzonen |
| `.../ui/home/CountdownCalculator.kt` | Rechnet Countdown aus `TripData` (Zeitzonen) |
| `.../ui/travel/TripInfoUi.kt` | Karte „Reise & Hotel“ (Flug, Gepäck, Hotel) |
| `.../ui/travel/BusData.kt` | KTEL-Kos-Fahrplan mit Quelle und Stand |
| `.../ui/travel/BusCardUi.kt` | Bus-Karte (verifiziert / nicht verifiziert) |
| `.../ui/travel/PhotoSpotsContent.kt` | Foto-Spots, nur Kos |
| `.../ui/travel/TravelUi.kt` | Vor-Ort-Seite, vereinfacht (ohne Live-Transit und Events) |
| `app/src/test/.../TripDataTest.kt`, `NoRhodesLeftoversTest.kt`, `BusDataTest.kt` | Neue Tests |

Entfernt wird: `ui/news/*`, `ui/settings/AppUpdate*`, `UpdateUi`, `ui/travel/{Lindos*,Kolymbia*,LiveTravel*,TransitPdfUi,TravelTranslation}`, `widget/BusTimetableCheckWorker`, `worker/`, `site/`, `SPEC.md`, `README.md`, `keystore.properties`, `rhodos-release-key.jks`.

---

### Task 1: Projektgerüst kopieren und umbenennen

**Files:**
- Create: `NEW/*` (Kopie aus `OLD`)
- Modify: `NEW/settings.gradle.kts`, `NEW/gradle.properties`, `NEW/app/build.gradle.kts`, `NEW/app/src/main/res/values/strings.xml`

- [ ] **Step 1: Dateien kopieren (ohne Build-Ordner, Worker, Site, Schlüssel, Doku)**

```bash
OLD=/c/Claude_Code_Projects/kos-countdown-widget
NEW=/c/Claude_Code_Projects/kos-urlaub
mkdir -p "$NEW/app"
cp -r "$OLD/gradle" "$OLD/gradlew" "$OLD/gradlew.bat" "$OLD/gradle.properties" \
      "$OLD/settings.gradle.kts" "$OLD/build.gradle.kts" "$OLD/local.properties" \
      "$OLD/Google-Maps-Links.csv" "$OLD/IMAGE_CREDITS.md" "$NEW/"
cp -r "$OLD/app/src" "$NEW/app/src"
cp "$OLD/app/build.gradle.kts" "$NEW/app/build.gradle.kts"
ls "$OLD/app" | grep -i proguard && cp "$OLD"/app/proguard-rules.pro "$NEW/app/" || true
ls "$NEW" "$NEW/app"
```

Expected: `NEW` enthält `app gradle gradlew gradlew.bat gradle.properties settings.gradle.kts build.gradle.kts local.properties Google-Maps-Links.csv IMAGE_CREDITS.md docs`; kein `worker`, `site`, `*.jks`.

- [ ] **Step 2: Paket, Ordner und Namen umbenennen**

```bash
cd /c/Claude_Code_Projects/kos-urlaub
for s in main test androidTest; do
  mv app/src/$s/java/com/example/koswidget app/src/$s/java/com/example/kosurlaub
done
grep -rlE "com[./]example[./]koswidget" . --include=*.kt --include=*.xml --include=*.kts \
  | xargs sed -i -E 's#com\.example\.koswidget#com.example.kosurlaub#g; s#com/example/koswidget#com/example/kosurlaub#g'
sed -i 's/rootProject.name = "Kos Widget"/rootProject.name = "Kos Urlaub"/' settings.gradle.kts
sed -i 's#<string name="app_name">Kos Widget</string>#<string name="app_name">Kos Urlaub</string>#' app/src/main/res/values/strings.xml
sed -i '/^NEWS_API_URL=/d' gradle.properties
grep -rn "koswidget" . --include=*.kt --include=*.xml --include=*.kts | head
```

Expected: letzter `grep` ohne Treffer.

- [ ] **Step 3: NEWS_API_URL und Signatur aus `app/build.gradle.kts` entfernen**

In `app/build.gradle.kts` diese Zeilen löschen:
`val newsApiUrl = providers.gradleProperty("NEWS_API_URL").orElse("")` und
`buildConfigField("String", "NEWS_API_URL", "\"${newsApiUrl.get()}\"")`.
Außerdem `implementation(libs.google.mlkit.translate)` aus `dependencies` und in `gradle/libs.versions.toml` die Zeilen `mlkitTranslate = "17.0.3"` und `google-mlkit-translate = ...` löschen. Der Block `signingConfigs` bleibt (er greift nur, wenn `keystore.properties` existiert, und die Datei wird nicht kopiert).

- [ ] **Step 4: Ausgangsstand bauen**

Run: `cd /c/Claude_Code_Projects/kos-urlaub && ./gradlew.bat :app:assembleDebug -q 2>&1 | tail -20`

Expected: Es kommen Compile-Fehler, weil `BuildConfig.NEWS_API_URL` in `MainActivity.kt` fehlt. Das ist erwartet und wird in Task 2 behoben. Ab Task 2 gilt wieder „grün“.

- [ ] **Step 5 (optional): Checkpoint**

```bash
cd /c/Claude_Code_Projects/kos-urlaub && git init -q && git add -A && git commit -qm "chore: fork aus kos-countdown-widget, Paket kosurlaub"
```

---

### Task 2: News und Updater entfernen

**Files:**
- Delete: `PKG/ui/news/` (komplett), `PKG/ui/settings/AppUpdateController.kt`, `AppUpdateRepository.kt`, `UpdateUi.kt`
- Delete: `app/src/test/java/com/example/kosurlaub/{NewsParserTest,NewsViewModelTest,TravelAlertsTest,VersionCompareTest}.kt`
- Modify: `PKG/MainActivity.kt`, `PKG/ui/settings/SettingsUi.kt`, `app/src/main/AndroidManifest.xml`, `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Dateien löschen**

```bash
cd /c/Claude_Code_Projects/kos-urlaub/app/src
rm -r main/java/com/example/kosurlaub/ui/news
rm main/java/com/example/kosurlaub/ui/settings/{AppUpdateController,AppUpdateRepository,UpdateUi}.kt
rm test/java/com/example/kosurlaub/{NewsParserTest,NewsViewModelTest,TravelAlertsTest,VersionCompareTest}.kt
```

- [ ] **Step 2: `MainActivity.kt` – Navigation neu schreiben**

Ersetze in `PKG/MainActivity.kt` alles ab der Zeile `private enum class MainDestination {` bis vor die Zeile `@OptIn(ExperimentalMaterial3Api::class)` (die zu `KosHome` gehört) durch:

```kotlin
private enum class MainDestination {
    HOME,
    COMPASS,
    TRAVEL
}

@Composable
private fun KosApp(startInTravel: Boolean = false) {
    val context = LocalContext.current
    val destinationName = rememberSaveable {
        mutableStateOf(if (startInTravel) MainDestination.TRAVEL.name else MainDestination.HOME.name)
    }
    val nestedDetailVisible = remember { mutableStateOf(false) }
    val scrollToTopRequest = remember { mutableIntStateOf(0) }
    val destination = MainDestination.valueOf(destinationName.value)

    BackHandler(enabled = !nestedDetailVisible.value && destination != MainDestination.HOME) {
        destinationName.value = MainDestination.HOME.name
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF0C252B),
        bottomBar = {
            if (!nestedDetailVisible.value) {
                MainNavigationBar(
                    selected = destination,
                    onSelect = { selected ->
                        if (selected == destination) scrollToTopRequest.intValue++
                        destinationName.value = selected.name
                    }
                )
            }
        }
    ) { padding ->
        when (destination) {
            MainDestination.HOME -> KosHome(
                padding = padding,
                scrollToTopRequest = scrollToTopRequest.intValue,
                onOpenTravel = { destinationName.value = MainDestination.TRAVEL.name }
            )
            MainDestination.TRAVEL -> TravelScreen(
                padding = padding,
                scrollToTopRequest = scrollToTopRequest.intValue,
                onBack = { destinationName.value = MainDestination.HOME.name },
                onDetailVisibilityChanged = { nestedDetailVisible.value = it }
            )
            MainDestination.COMPASS -> CompassScreen(
                padding = padding,
                scrollToTopRequest = scrollToTopRequest.intValue,
                onOpenCommunity = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(COMMUNITY_URL)))
                },
                onDetailVisibilityChanged = { nestedDetailVisible.value = it }
            )
        }
    }
}

@Composable
private fun MainNavigationBar(
    selected: MainDestination,
    onSelect: (MainDestination) -> Unit
) {
    NavigationBar(containerColor = Color(0xFF102A2F)) {
        MainDestination.entries.forEach { destination ->
            val labelRes = when (destination) {
                MainDestination.HOME -> R.string.main_nav_home
                MainDestination.COMPASS -> R.string.main_nav_compass
                MainDestination.TRAVEL -> R.string.main_nav_travel
            }
            val iconRes = when (destination) {
                MainDestination.HOME -> R.drawable.ic_nav_home
                MainDestination.COMPASS -> R.drawable.ic_nav_compass
                MainDestination.TRAVEL -> R.drawable.ic_nav_travel
            }
            NavigationBarItem(
                selected = destination == selected,
                onClick = { onSelect(destination) },
                icon = { Icon(painter = painterResource(iconRes), contentDescription = null) },
                label = { Text(stringResource(labelRes)) },
                modifier = Modifier.testTag("main-nav-${destination.name.lowercase()}"),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF102A2F),
                    selectedTextColor = HomeAccent,
                    indicatorColor = HomeAccent,
                    unselectedIconColor = Color(0xB3FFFFFF),
                    unselectedTextColor = Color(0xB3FFFFFF)
                )
            )
        }
    }
}
```

Außerdem in `MainActivity.kt`:
1. In `onCreate` die Zeile `BusTimetableCheckWorker.schedule(applicationContext)` löschen.
2. Konstanten `NAVIGATION_PREFS` und `LAST_SEEN_NEWS_ID` (oben in der Datei) löschen.
3. `KosHome`: Signatur auf `(padding: PaddingValues, scrollToTopRequest: Int, onOpenTravel: () -> Unit = {})` kürzen. Entfernen: `updateController`, das `LaunchedEffect(updateController)`, den `UpdateBanner`-Block, die Update-Parameter von `HeaderSection(...)` und `SettingsSheet(...)` (`hasUpdateBadge`, `showNewsBadge`, `onOpenNews`, `isCheckingUpdate`, `hasUpdate`, `onCheckUpdate`) und den ganzen `if (updateController.showStartupDialog) { ... }`-Block sowie `showUpdateDownloadError`. In `HomeQuickActions(...)` wird `onOpenTigaki = onOpenTravel` übergeben.
4. `HeaderSection`: Parameter `hasUpdateBadge`, `showNewsBadge`, `onOpenNews` und deren Glocken-/Badge-Code entfernen. Die Zahnrad-Schaltfläche (`onSettings`) bleibt.
5. In `ui/settings/SettingsUi.kt` von `SettingsSheet` die Parameter `isCheckingUpdate`, `hasUpdate`, `onCheckUpdate` und die zugehörige Update-Zeile entfernen. `onShare` und `onOpenGallery` bleiben.
6. Nicht mehr benutzte Imports löschen (der Compiler meldet sie als Warnung, `./gradlew.bat :app:compileDebugKotlin` zeigt sie).

- [ ] **Step 3: Manifest und Strings bereinigen**

In `app/src/main/AndroidManifest.xml` löschen:
- `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />` samt Kommentar darüber,
- den gesamten `<provider ... FileProvider ...>`-Block.
In `app/src/main/res/xml/` die Datei `file_paths.xml` löschen. In `strings.xml` die Zeile `<string name="main_nav_news">Aktuelles</string>` löschen.
`POST_NOTIFICATIONS` bleibt vorerst (wird in Task 3 geprüft).

- [ ] **Step 4: Kompilieren und Fehler abarbeiten**

Run: `cd /c/Claude_Code_Projects/kos-urlaub && ./gradlew.bat :app:compileDebugKotlin -q 2>&1 | grep -E "^e: " | head -30`

Jeder Fehler nennt Datei und Zeile. Vorgehen: nur die gemeldete Verwendung von News/Update/Events entfernen (nicht mehr). Erwartete Restfehler liegen in `TravelUi.kt` (`LiveTravelRepository`, `TravelAlertsCard`, `TravelTranslation`, `KosEvent`); diese verschwinden in Task 3, wenn `TravelScreen` ersetzt wird. Bleiben nur Fehler aus `ui/travel/`, ist Task 2 fertig.

- [ ] **Step 5 (optional): Checkpoint**

`git add -A && git commit -qm "refactor: News und Updater entfernt"`

---

### Task 3: Rhodos-Fahrplan-System entfernen und Vor-Ort-Seite vereinfachen

**Files:**
- Delete: `PKG/ui/travel/{KolymbiaTimetable,LindosTimetable,LindosTimetableUi,LiveTravelData,LiveTravelUi,TransitPdfUi,TravelTranslation}.kt`, `PKG/widget/BusTimetableCheckWorker.kt`
- Delete: `app/src/test/.../{KolymbiaTimetableTest,LindosTimetableTest,LiveTravelDataTest}.kt`
- Modify: `PKG/ui/travel/TravelUi.kt`, `PKG/ui/travel/TravelContent.kt`, `PKG/ui/travel/TravelExtraUi.kt`, `app/src/androidTest/.../MainActivitySmokeTest.kt`, `strings.xml`

- [ ] **Step 1: Dateien löschen**

```bash
cd /c/Claude_Code_Projects/kos-urlaub/app/src
rm main/java/com/example/kosurlaub/ui/travel/{KolymbiaTimetable,LindosTimetable,LindosTimetableUi,LiveTravelData,LiveTravelUi,TransitPdfUi,TravelTranslation}.kt
rm main/java/com/example/kosurlaub/widget/BusTimetableCheckWorker.kt
rm test/java/com/example/kosurlaub/{KolymbiaTimetableTest,LindosTimetableTest,LiveTravelDataTest}.kt
```

- [ ] **Step 2: `TravelScreen` ersetzen**

In `PKG/ui/travel/TravelUi.kt` die Funktion `TravelScreen` (von `@Composable\nfun TravelScreen(` bis vor `@Composable\nprivate fun TravelOverviewScreen(`) durch dieses Skript ersetzen:

```bash
cd /c/Claude_Code_Projects/kos-urlaub && python - <<'PY'
p = "app/src/main/java/com/example/kosurlaub/ui/travel/TravelUi.kt"
s = open(p, encoding="utf-8").read()
a = s.index("@Composable\nfun TravelScreen(")
b = s.index("@Composable\nprivate fun TravelOverviewScreen(")
new = '''@Composable
fun TravelScreen(
    padding: PaddingValues,
    scrollToTopRequest: Int = 0,
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
    var selectedArea by rememberSaveable { mutableStateOf(TravelArea.TODAY) }
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
            .background(Brush.verticalGradient(listOf(Color(0xFF142E34), Color(0xFF0D1113))))
            .padding(padding)
            .testTag("travel-area-screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TravelSubTabRow(
                selectedArea = selectedArea,
                onSelectArea = { selectedArea = it }
            )
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
                item { TavernCalculatorCard() }
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
                item { KosEmergencyCard(onCall = callNumber, onOpenMap = openMap) }
                item { EmergencyCard(onCall = { callNumber("112") }) }
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
                    item {
                        TravelMapHelpCard(
                            onHospital = { openMap("General Hospital of Kos") },
                            onPharmacy = { openMap("pharmacy near me") }
                        )
                    }
                    item { SupportingTravelText(R.string.travel_emergency_disclaimer) }
                }
            }
        }
    }
}

'''
open(p, "w", encoding="utf-8").write(s[:a] + new + s[b:])
PY
```

Hinweis: `TripInfoCard`, `BusCard`, `KtelKosTimetable` entstehen in Task 5 und 8. Bis dahin Stubs anlegen (Step 3).

- [ ] **Step 3: Stubs, damit es kompiliert**

Neue Datei `PKG/ui/travel/TripInfoUi.kt`:

```kotlin
package com.example.kosurlaub.ui.travel

import androidx.compose.runtime.Composable

@Composable
internal fun TripInfoCard(onOpenMap: (String) -> Unit) {
}
```

Neue Datei `PKG/ui/travel/BusData.kt`:

```kotlin
package com.example.kosurlaub.ui.travel

internal object KtelKosTimetable {
    const val OFFICIAL_URL = "https://www.ktel-kos.gr/"
}
```

Neue Datei `PKG/ui/travel/BusCardUi.kt`:

```kotlin
package com.example.kosurlaub.ui.travel

import androidx.compose.runtime.Composable

@Composable
internal fun BusCard(onOpenSite: () -> Unit) {
}
```

- [ ] **Step 4: Rhodos-Bausteine löschen**

In `TravelUi.kt`: die Funktionen `MobilityHubCard` und `KosEmergencyCard`-Inhalt bleiben noch (wird in Task 6 ersetzt), aber `MobilityHubCard` komplett löschen. In `TravelExtraUi.kt` die Funktion `OfflineTravelSummaryCard` löschen. In `TravelContent.kt`: den Eintrag mit `url = "https://www.ktelrodou.gr/schedule/"` aus `travelSources` entfernen und `"www.ktelrodou.gr"` aus der Liste der vertrauten Hosts (Zeile um 149) löschen. In `strings.xml` die Einträge `travel_pdf_ktel_help`, `travel_pdf_roda_help`, `travel_bus_tsambika`, `travel_ktel_title`, `travel_ktel_description` und alle Strings, die der Compiler als ungenutzt zeigt (`./gradlew.bat :app:lintDebug` ist optional), entfernen.

- [ ] **Step 5: Kompilieren, Tests, Smoke-Test bereinigen**

Run: `cd /c/Claude_Code_Projects/kos-urlaub && ./gradlew.bat :app:compileDebugKotlin -q 2>&1 | grep -E "^e: " | head -30`

Alle verbleibenden Fehler abarbeiten, indem nur die gemeldete Verwendung gelöscht wird. Danach `MainActivitySmokeTest.kt`: Tests löschen, die `main-nav-news`, `compass-tip-hotel-relax-tigaki`, Fahrplan, Updates oder News ansprechen. Beim Löschen immer ganze `@Test`-Funktionen entfernen.

Run: `./gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:compileDebugAndroidTestKotlin -q 2>&1 | tail -20`

Expected: keine Ausgabe (grün).

- [ ] **Step 6 (optional): Checkpoint**

`git add -A && git commit -qm "refactor: Rhodos-Fahrplan-System entfernt, Vor-Ort-Seite vereinfacht"`

---

### Task 4: `TripData` – einzige Quelle für Reisedaten

**Files:**
- Create: `PKG/TripData.kt`, `app/src/test/java/com/example/kosurlaub/TripDataTest.kt`

- [ ] **Step 1: Failing test schreiben**

`app/src/test/java/com/example/kosurlaub/TripDataTest.kt`:

```kotlin
package com.example.kosurlaub

import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TripDataTest {
    private fun millis(y: Int, m: Int, d: Int, h: Int, min: Int, zone: String) =
        ZonedDateTime.of(y, m, d, h, min, 0, 0, ZoneId.of(zone)).toInstant().toEpochMilli()

    @Test
    fun `outbound flight uses local airport times`() {
        assertEquals(millis(2027, 9, 13, 13, 25, "Europe/Berlin"), TripData.outbound.departureMillis())
        assertEquals(millis(2027, 9, 13, 17, 45, "Europe/Athens"), TripData.outbound.arrivalMillis())
    }

    @Test
    fun `inbound flight uses local airport times`() {
        assertEquals(millis(2027, 9, 28, 11, 35, "Europe/Athens"), TripData.inbound.departureMillis())
        assertEquals(millis(2027, 9, 28, 14, 0, "Europe/Berlin"), TripData.inbound.arrivalMillis())
    }

    @Test
    fun `flight durations are plausible`() {
        val outbound = TripData.outbound.arrivalMillis() - TripData.outbound.departureMillis()
        val inbound = TripData.inbound.arrivalMillis() - TripData.inbound.departureMillis()
        assertEquals(3 * 3_600_000L + 20 * 60_000L, outbound)
        assertEquals(3 * 3_600_000L + 25 * 60_000L, inbound)
    }

    @Test
    fun `summary shows local times and flight number`() {
        assertEquals(
            "Mo 13.09.2027 · Hamburg 13:25 → Kos 17:45 (Ortszeit) · Condor DE1640",
            TripData.outbound.summary()
        )
        assertEquals(
            "Di 28.09.2027 · Kos 11:35 → Hamburg 14:00 (Ortszeit) · Condor DE1641",
            TripData.inbound.summary()
        )
    }

    @Test
    fun `hotel coordinates are on Kos`() {
        assertTrue(TripData.HOTEL_LAT in 36.7..36.95)
        assertTrue(TripData.HOTEL_LON in 26.9..27.4)
    }
}
```

- [ ] **Step 2: Test laufen lassen, Fehlschlag prüfen**

Run: `cd /c/Claude_Code_Projects/kos-urlaub && ./gradlew.bat :app:testDebugUnitTest --tests "*TripDataTest" -q 2>&1 | head -5`
Expected: FAIL, `Unresolved reference: TripData`.

- [ ] **Step 3: Implementierung**

`PKG/TripData.kt`:

```kotlin
package com.example.kosurlaub

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** Einzige Quelle für alle Reisedaten. Alle Uhrzeiten sind Ortszeiten des jeweiligen Flughafens. */
object TripData {
    data class Flight(
        val airline: String,
        val number: String,
        val weekday: String,
        val year: Int,
        val month: Int,
        val day: Int,
        val fromCity: String,
        val fromAirport: String,
        val fromZone: String,
        val departHour: Int,
        val departMinute: Int,
        val toCity: String,
        val toAirport: String,
        val toZone: String,
        val arriveHour: Int,
        val arriveMinute: Int
    ) {
        fun departureMillis(): Long = millis(fromZone, year, month, day, departHour, departMinute)
        fun arrivalMillis(): Long = millis(toZone, year, month, day, arriveHour, arriveMinute)

        fun summary(): String = String.format(
            Locale.GERMAN,
            "%s %02d.%02d.%d · %s %02d:%02d → %s %02d:%02d (Ortszeit) · %s %s",
            weekday, day, month, year,
            fromCity, departHour, departMinute,
            toCity, arriveHour, arriveMinute,
            airline, number
        )
    }

    val outbound = Flight(
        airline = "Condor", number = "DE1640", weekday = "Mo",
        year = 2027, month = 9, day = 13,
        fromCity = "Hamburg", fromAirport = "HAM", fromZone = "Europe/Berlin",
        departHour = 13, departMinute = 25,
        toCity = "Kos", toAirport = "KGS", toZone = "Europe/Athens",
        arriveHour = 17, arriveMinute = 45
    )

    val inbound = Flight(
        airline = "Condor", number = "DE1641", weekday = "Di",
        year = 2027, month = 9, day = 28,
        fromCity = "Kos", fromAirport = "KGS", fromZone = "Europe/Athens",
        departHour = 11, departMinute = 35,
        toCity = "Hamburg", toAirport = "HAM", toZone = "Europe/Berlin",
        arriveHour = 14, arriveMinute = 0
    )

    const val CHECKED_BAG_KG = 23
    const val CARRY_ON_KG = 8
    const val CARRY_ON_SIZE = "40 x 55 x 20 cm"
    const val OPERATOR = "ByeBye"
    const val ROOM = "Doppelzimmer mit Klimaanlage, Bad oder Dusche, Balkon oder Terrasse"
    const val BOARD = "All Inclusive"

    const val HOTEL_NAME = "ILIOS.K Village Resort"
    const val HOTEL_PLACE = "Tigaki"
    const val HOTEL_POSTAL_CODE = "85300"
    const val HOTEL_LAT = 36.8800
    const val HOTEL_LON = 27.1834
    const val HOTEL_CHECK_IN = "ab 15:00 Uhr"
    const val HOTEL_CHECK_OUT = "bis 11:00 Uhr"

    /** Nur auf top-hotels-gr.com gefunden, nicht gegengeprüft (Vorwahl 22420 passt zu Kos). */
    const val HOTEL_PHONE_UNCONFIRMED = "+30 22420 69411"

    private fun millis(zone: String, y: Int, m: Int, d: Int, h: Int, min: Int): Long =
        Calendar.getInstance(TimeZone.getTimeZone(zone)).apply {
            clear()
            set(y, m - 1, d, h, min, 0)
        }.timeInMillis
}
```

- [ ] **Step 4: Test grün**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "*TripDataTest" -q 2>&1 | head -5`
Expected: keine Ausgabe.

- [ ] **Step 5 (optional): Checkpoint**

`git add -A && git commit -qm "feat: TripData als einzige Quelle fuer Reisedaten"`

---

### Task 5: Countdown, Wetter und Hotelkarte auf `TripData` umstellen

**Files:**
- Modify: `PKG/ui/home/CountdownCalculator.kt`, `PKG/ui/home/HomeState.kt`, `PKG/ui/weather/Weather.kt`, `PKG/ui/weather/MarineWeather.kt`, `app/src/test/.../CountdownCalculatorTest.kt`
- Create: `PKG/ui/travel/TripInfoUi.kt` (ersetzt Stub)

- [ ] **Step 1: Tests anpassen (zuerst, sie sollen fehlschlagen)**

In `CountdownCalculatorTest.kt` die Hilfsfunktion `getMockToday` so ändern, dass sie in Hamburger Zeit rechnet, und die Zeiten an Abflug 13:25 und Ankunft 16:45 (Berliner Zeit) anpassen:

```kotlin
    private fun getMockToday(year: Int, month: Int, day: Int, hour: Int = 12, minute: Int = 0): Calendar {
        return Calendar.getInstance(java.util.TimeZone.getTimeZone("Europe/Berlin")).apply {
            clear()
            set(year, month, day, hour, minute, 0)
        }
    }
```

und diese beiden Tests ersetzen:

```kotlin
    @Test
    fun `isOnVacation is false before arrival in Kos`() {
        val beforeArrival = getMockToday(2027, Calendar.SEPTEMBER, 13, 16, 44)
        assertFalse(CountdownCalculator.calculate(beforeArrival).isOnVacation)
    }

    @Test
    fun `isOnVacation is true after arrival in Kos`() {
        val afterArrival = getMockToday(2027, Calendar.SEPTEMBER, 13, 16, 46)
        val r = CountdownCalculator.calculate(afterArrival)
        assertTrue(r.isReached)
        assertTrue(r.isOnVacation)
    }
```

(Der bisherige Test `isOnVacation is false before departure` mit `13, 13, 24` bleibt.)

Run: `./gradlew.bat :app:testDebugUnitTest --tests "*CountdownCalculatorTest" -q 2>&1 | head -8` → die neuen Tests laufen noch gegen die alte Logik und dürfen hier schon grün sein; der Wert dieses Schritts ist, dass die Tests unabhängig von der Gerätezeitzone sind.

- [ ] **Step 2: `CountdownCalculator.kt` auf `TripData` umstellen**

Ersetze in `PKG/ui/home/CountdownCalculator.kt` den Block von `// Condor DE1640 ...` bis einschließlich `private fun departureCalendar(...)` am Ende des Objekts durch:

```kotlin
    // Startpunkt des Countdowns (genau 1 Jahr vor Abflug)
    private const val START_YEAR = 2026
    private const val START_MONTH = Calendar.SEPTEMBER
    private const val START_DAY = 13

    private fun calendarAt(millis: Long, zone: String) =
        Calendar.getInstance(TimeZone.getTimeZone(zone)).apply { timeInMillis = millis }

    /** Abflug in Hamburger Ortszeit. */
    fun departureCalendar(): Calendar =
        calendarAt(TripData.outbound.departureMillis(), TripData.outbound.fromZone)

    private fun arrivalCalendar(): Calendar =
        calendarAt(TripData.outbound.arrivalMillis(), TripData.outbound.toZone)

    fun progressFraction(now: Calendar = Calendar.getInstance()): Float {
        val start = Calendar.getInstance().apply {
            set(START_YEAR, START_MONTH, START_DAY, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val total = (TripData.outbound.departureMillis() - start.timeInMillis).toFloat()
        val elapsed = (now.timeInMillis - start.timeInMillis).toFloat()
        return (elapsed / total).coerceIn(0f, 1f)
    }

    fun calculate(now: Calendar = Calendar.getInstance()): RemainingTime {
        val remainingMillis = (TripData.outbound.departureMillis() - now.timeInMillis).coerceAtLeast(0L)
        val days = TimeUnit.MILLISECONDS.toDays(remainingMillis)
        val afterDays = remainingMillis - TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(afterDays)
        val afterHours = afterDays - TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(afterHours)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(afterHours - TimeUnit.MINUTES.toMillis(minutes))
        val departureDayStart = midnight(departureCalendar())
        return RemainingTime(
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            isReached = now.timeInMillis >= departureDayStart.timeInMillis,
            isOnVacation = now.timeInMillis >= TripData.outbound.arrivalMillis()
        )
    }

    fun daysUntilDeparture(now: Calendar = Calendar.getInstance()): Int {
        val today = midnight(now.clone() as Calendar)
        val departure = midnight(departureCalendar())
        val diffDays = (departure.timeInMillis - today.timeInMillis) / 86_400_000.0
        return Math.round(diffDays).toInt()
    }

    fun midnight(cal: Calendar): Calendar = cal.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    fun isFlightDay(now: Calendar = Calendar.getInstance()): Boolean {
        val departure = departureCalendar()
        val local = Calendar.getInstance(departure.timeZone).apply { timeInMillis = now.timeInMillis }
        return local.get(Calendar.YEAR) == departure.get(Calendar.YEAR) &&
            local.get(Calendar.DAY_OF_YEAR) == departure.get(Calendar.DAY_OF_YEAR)
    }
}
```

Imports ergänzen: `import com.example.kosurlaub.TripData` und `import java.util.TimeZone`. Die Funktion `toShortGermanWeekday` unterhalb des Objekts bleibt unverändert. `arrivalCalendar()` wird nicht mehr benutzt und kann gelöscht werden, falls der Compiler sie als ungenutzt meldet.

- [ ] **Step 3: `HomeState.kt` anpassen**

Die fünf Zeilen `private val YEAR/MONTH/DAY/HOUR/MINUTE = CountdownCalculator.DEPARTURE_...` löschen. Den Block `val df = ... val target = Calendar.getInstance().apply { ... }` ersetzen durch:

```kotlin
            val berlin = java.util.TimeZone.getTimeZone(TripData.outbound.fromZone)
            val df = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).apply { timeZone = berlin }
            val tf = SimpleDateFormat("HH:mm", Locale.GERMAN).apply { timeZone = berlin }
            val target = CountdownCalculator.departureCalendar()
```

Import `com.example.kosurlaub.TripData` ergänzen. So zeigt die App „13.09.2027 · 13:25 Uhr“ auch dann, wenn das Handy später in Griechenland auf Kos-Zeit steht.

- [ ] **Step 4: Wetter-Koordinaten aus `TripData`**

In `Weather.kt`: `private const val LATITUDE = 36.8800` und `LONGITUDE = 27.1834` ersetzen durch `private const val LATITUDE = TripData.HOTEL_LAT` und `private const val LONGITUDE = TripData.HOTEL_LON`. In `MarineWeather.kt` den Teil `"?latitude=36.8903&longitude=27.1826"` ersetzen durch `"?latitude=${TripData.HOTEL_LAT}&longitude=${TripData.HOTEL_LON}"`. Beide Dateien: `import com.example.kosurlaub.TripData`.

- [ ] **Step 5: `TripInfoCard` (ersetzt den Stub)**

`PKG/ui/travel/TripInfoUi.kt`:

```kotlin
package com.example.kosurlaub.ui.travel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.theme.HomeAccent

@Composable
internal fun TripInfoCard(onOpenMap: (String) -> Unit) {
    TravelCardContainer {
        Text(
            "REISE & HOTEL ✈️",
            color = HomeAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.testTag("trip-info-card")
        )
        Spacer(Modifier.height(8.dp))
        InfoLine("Hinflug", TripData.outbound.summary())
        InfoLine("Rückflug", TripData.inbound.summary())
        InfoLine(
            "Gepäck",
            "${TripData.CHECKED_BAG_KG} kg aufgegeben, ${TripData.CARRY_ON_KG} kg Handgepäck " +
                "(max. ${TripData.CARRY_ON_SIZE}) pro Person"
        )
        InfoLine("Hotel", "${TripData.HOTEL_NAME}, ${TripData.HOTEL_PLACE}, ${TripData.HOTEL_POSTAL_CODE} Kos")
        InfoLine("Zimmer", "${TripData.ROOM}, ${TripData.BOARD}")
        InfoLine("Check-in / Check-out", "${TripData.HOTEL_CHECK_IN} / ${TripData.HOTEL_CHECK_OUT}")
        InfoLine("Veranstalter", TripData.OPERATOR)
        Spacer(Modifier.height(4.dp))
        TextButton(
            onClick = { onOpenMap("${TripData.HOTEL_LAT},${TripData.HOTEL_LON}(${TripData.HOTEL_NAME})") },
            modifier = Modifier.testTag("trip-info-map")
        ) {
            Text("📍 Hotel auf der Karte", color = HomeAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun InfoLine(label: String, text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label.uppercase(), color = Color(0xBFFFFFFF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text, color = Color.White, fontSize = 12.sp, lineHeight = 17.sp)
    }
}
```

Zusätzlicher Import für `padding`: `import androidx.compose.foundation.layout.padding`.

- [ ] **Step 6: Alle Tests und Build**

Run: `./gradlew.bat :app:testDebugUnitTest :app:assembleDebug -q 2>&1 | tail -15`
Expected: keine Ausgabe.

- [ ] **Step 7 (optional): Checkpoint**

`git add -A && git commit -qm "feat: Countdown, Wetter und Reise-Karte nutzen TripData"`

---

### Task 6: SOS-Karte mit richtigen Nummern

**Files:**
- Modify: `PKG/ui/travel/TravelUi.kt` (Funktion `KosEmergencyCard`)

- [ ] **Step 1: Inhalt der Spalte ersetzen**

In `KosEmergencyCard` den Block `Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { ... }` ersetzen durch:

```kotlin
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EmergencyContactRow("EU-Notruf (Rettung & Polizei)", "112", onCall)
            EmergencyContactRow("Polizei", "100", onCall)
            EmergencyContactRow("Rettungsdienst (Krankenwagen)", "166", onCall)
            EmergencyContactRow("Feuerwehr", "199", onCall)
            EmergencyContactRow(
                "Hotel-Rezeption (Nummer nur aus einer Quelle, nicht bestätigt)",
                TripData.HOTEL_PHONE_UNCONFIRMED,
                onCall,
                onOpenMap = {
                    onOpenMap("${TripData.HOTEL_LAT},${TripData.HOTEL_LON}(${TripData.HOTEL_NAME})")
                }
            )
        }
```

Import `com.example.kosurlaub.TripData` in `TravelUi.kt` ergänzen. Der Titel `NOTFALL & SERVICE TIGAKI 🚨` und `Wichtige Telefonnummern & Anlaufstellen vor Ort` bleiben.

- [ ] **Step 2: Test und Build**

Run: `./gradlew.bat :app:testDebugUnitTest :app:assembleDebug -q 2>&1 | tail -10`
Expected: keine Ausgabe.

---

### Task 7: Foto-Spots nur für Kos

**Files:**
- Modify: `PKG/ui/travel/PhotoSpotsContent.kt`, `app/src/test/.../PhotoSpotsTest.kt`

- [ ] **Step 1: Test zuerst ändern**

In `PhotoSpotsTest.kt` den ersten Test ersetzen durch:

```kotlin
    @Test
    fun photoSpotsContainsEightKosSpots() {
        assertEquals(8, photoSpots.size)
    }
```

- [ ] **Step 2: Test fehlschlagen lassen**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "*PhotoSpotsTest" -q 2>&1 | head -6`
Expected: FAIL (`expected:<8> but was:<10>`).

- [ ] **Step 3: Inhalt ersetzen**

In `PhotoSpotsContent.kt` die Zuweisung `internal val photoSpots = listOf(...)` ersetzen (die `data class PhotoSpot(...)` oberhalb bleibt):

```kotlin
internal val photoSpots = listOf(
    PhotoSpot(
        id = "tigaki_beach",
        title = "Tigaki Beach am Morgen",
        location = "Tigaki",
        bestTime = "Frühmorgens (ca. 07:00–08:30 Uhr)",
        timeCategory = "Sonnenaufgang",
        tip = "Flaches Wasser und feiner Sand: Am frühen Morgen liegt das Meer oft spiegelglatt, die Berge der Türkei zeichnen sich im Dunst ab.",
        mapQuery = "Tigaki Beach, Kos",
        iconEmoji = "🌅"
    ),
    PhotoSpot(
        id = "alikes_flamingos",
        title = "Flamingos am Alikes-Salzsee",
        location = "Alikes, bei Tigaki",
        bestTime = "Früher Morgen oder später Nachmittag",
        timeCategory = "Natur",
        tip = "Kurzer Spaziergang von Tigaki. Mit Teleobjektiv oder Handy-Zoom Abstand halten, die Vögel sind scheu.",
        mapQuery = "Alikes Salt Lake, Tigaki, Kos",
        iconEmoji = "🦩"
    ),
    PhotoSpot(
        id = "neratzia_castle",
        title = "Johanniterburg Neratzia",
        location = "Kos-Stadt, am Hafen",
        bestTime = "Vormittag oder später Nachmittag",
        timeCategory = "Vormittag",
        tip = "Von der Brücke am Eingang und vom Hafenkai aus wirken die Mauern am besten. Mittags ist das Licht sehr hart.",
        mapQuery = "Castle of Neratzia, Kos",
        iconEmoji = "🏰"
    ),
    PhotoSpot(
        id = "hippocrates_plane",
        title = "Platane des Hippokrates",
        location = "Kos-Stadt, Platia Platanou",
        bestTime = "Vormittag",
        timeCategory = "Vormittag",
        tip = "Der alte Baum steht gestützt im Schatten; Detailfotos der Stützen und des Brunnens daneben funktionieren gut.",
        mapQuery = "Hippocrates Plane Tree, Kos",
        iconEmoji = "🌳"
    ),
    PhotoSpot(
        id = "asklepieion",
        title = "Asklepieion mit Meerblick",
        location = "bei Kos-Stadt",
        bestTime = "Vormittag (nach Öffnung, wenig Besucher)",
        timeCategory = "Vormittag",
        tip = "Von den oberen Terrassen reicht der Blick über die Küste bis zur Türkei. Die Säulen der obersten Ebene sind ein gutes Motiv.",
        mapQuery = "Asklepieion of Kos",
        iconEmoji = "🏛️"
    ),
    PhotoSpot(
        id = "zia_sunset",
        title = "Zia bei Sonnenuntergang",
        location = "Zia, Berge über Kos",
        bestTime = "Sonnenuntergang (Mitte September ca. 19:30–20:00 Uhr)",
        timeCategory = "Golden Hour",
        tip = "Gut eine halbe Stunde vor Sonnenuntergang da sein, damit ihr einen Platz mit freiem Blick habt. Rückfahrt vorher klären.",
        mapQuery = "Zia Village, Kos",
        iconEmoji = "🌇"
    ),
    PhotoSpot(
        id = "kastri_kefalos",
        title = "Kastri-Insel bei Kefalos",
        location = "Kefalos, Südwesten",
        bestTime = "Später Nachmittag",
        timeCategory = "Golden Hour",
        tip = "Die kleine Insel mit der weißen Kapelle liegt direkt vor dem Strand. Bei Niedrigwasser sieht man den Sandweg dorthin.",
        mapQuery = "Kastri Island, Kefalos, Kos",
        iconEmoji = "⛪"
    ),
    PhotoSpot(
        id = "paradise_beach",
        title = "Paradise Beach und Dünen",
        location = "Kefalos-Bucht, Südküste",
        bestTime = "Vormittag",
        timeCategory = "Vormittag",
        tip = "Weite Sandstrände und Dünen. Am besten von oben fotografieren, dort zeigt sich die Farbe des Wassers.",
        mapQuery = "Paradise Beach, Kos",
        iconEmoji = "🏖️"
    )
)
```

Hinweis: Die Tipps sind allgemeine Fotohinweise, keine Behauptungen über Öffnungszeiten oder Preise. Nur „Mitte September ca. 19:30–20:00 Uhr“ ist eine Zeitangabe; sie ist als Näherung formuliert.

- [ ] **Step 4: Test grün**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "*PhotoSpotsTest" -q 2>&1 | head -6`
Expected: keine Ausgabe.

---

### Task 8: Bus-Fahrplan KTEL Kos mit Quelle und Stand

**Files:**
- Modify: `PKG/ui/travel/BusData.kt`, `PKG/ui/travel/BusCardUi.kt` (ersetzen die Stubs)
- Create: `app/src/test/java/com/example/kosurlaub/BusDataTest.kt`

- [ ] **Step 1: Test schreiben**

`BusDataTest.kt`:

```kotlin
package com.example.kosurlaub

import com.example.kosurlaub.ui.travel.KtelKosTimetable
import org.junit.Assert.assertTrue
import org.junit.Test

class BusDataTest {
    private val time = Regex("""([01]\d|2[0-3]):[0-5]\d""")

    @Test
    fun `every route has https source, stand and sorted valid times`() {
        KtelKosTimetable.routes.forEach { route ->
            assertTrue(route.source.startsWith("https://"))
            assertTrue(route.asOf.isNotBlank())
            assertTrue(route.departures.isNotEmpty())
            route.departures.forEach { assertTrue("Zeit ungültig: ${it.time}", time.matches(it.time)) }
            val times = route.departures.map { it.time }
            assertTrue("Zeiten nicht sortiert in ${route.from} → ${route.to}", times == times.sorted())
        }
    }

    @Test
    fun `official site is https`() {
        assertTrue(KtelKosTimetable.OFFICIAL_URL.startsWith("https://"))
    }
}
```

- [ ] **Step 2: Datenmodell**

`PKG/ui/travel/BusData.kt` (Stub ersetzen):

```kotlin
package com.example.kosurlaub.ui.travel

internal data class BusDeparture(val time: String, val note: String = "")

internal data class BusRoute(
    val from: String,
    val to: String,
    val validity: String,
    val departures: List<BusDeparture>,
    val source: String,
    val asOf: String
)

/** Fahrplan von KTEL Kos. Nur mit Quelle und Stand eintragen; leer heißt: nicht verifiziert. */
internal object KtelKosTimetable {
    const val OFFICIAL_URL = "https://www.ktel-kos.gr/"

    val routes: List<BusRoute> = emptyList()

    val isVerified: Boolean get() = routes.isNotEmpty()
}
```

- [ ] **Step 3: Echte Daten recherchieren und eintragen**

Quelle: `https://www.ktel-kos.gr/` (Fahrpläne „Kos – Tigaki“ und „Tigaki – Kos“, Gültigkeit für September 2027 beachten). Seite mit dem Browser oder `ctx_fetch_and_index` laden. Gibt es eine PDF-Datei, mit dem PDF-Werkzeug lesen. Dann für jede Richtung eine `BusRoute` in `routes` eintragen, zum Beispiel:

```kotlin
    val routes: List<BusRoute> = listOf(
        BusRoute(
            from = "Tigaki", to = "Kos-Stadt",
            validity = "<Gültigkeit laut Quelle>",
            departures = listOf(BusDeparture("07:30"), BusDeparture("09:00") /* ... alle Zeiten laut Quelle */),
            source = "https://www.ktel-kos.gr/<konkrete Seite>",
            asOf = "<Datum des Abrufs, TT.MM.JJJJ>"
        )
    )
```

Die Beispielwerte oben sind Platzhalter für die Form und dürfen nicht übernommen werden. Findet sich keine verlässliche Quelle für 2027 (häufig erscheinen Sommerfahrpläne erst im Frühjahr), bleibt `routes` leer. Dann zeigt die App „nicht verifiziert“ mit Link und die Karte ist trotzdem korrekt. Ergebnis (Quelle gefunden / nicht gefunden) in `docs/superpowers/plans/` unter „Notizen“ am Dateiende festhalten.

- [ ] **Step 4: Bus-Karte**

`PKG/ui/travel/BusCardUi.kt` (Stub ersetzen):

```kotlin
package com.example.kosurlaub.ui.travel

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.ui.theme.HomeAccent

@Composable
internal fun BusCard(onOpenSite: () -> Unit) {
    TravelCardContainer {
        Text(
            "BUS TIGAKI ↔ KOS-STADT 🚌",
            color = HomeAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.testTag("bus-card")
        )
        Spacer(Modifier.height(8.dp))
        if (KtelKosTimetable.isVerified) {
            KtelKosTimetable.routes.forEach { route ->
                Text(
                    "${route.from} → ${route.to}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(route.validity, color = Color(0xBFFFFFFF), fontSize = 10.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    route.departures.joinToString("  ·  ") { it.time },
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
                Text(
                    "Quelle: ${route.source} (Stand ${route.asOf})",
                    color = Color(0x99FFFFFF),
                    fontSize = 10.sp
                )
                Spacer(Modifier.height(10.dp))
            }
        } else {
            Text(
                "Eigener Fahrplan noch nicht verifiziert. Aktuelle Zeiten und Preise bitte bei KTEL Kos nachsehen.",
                color = Color(0xE6FFFFFF),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                modifier = Modifier.testTag("bus-unverified")
            )
        }
        TextButton(onClick = onOpenSite, modifier = Modifier.testTag("bus-open-site")) {
            Text("KTEL Kos öffnen", color = HomeAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
```

- [ ] **Step 5: Tests und Build**

Run: `./gradlew.bat :app:testDebugUnitTest :app:assembleDebug -q 2>&1 | tail -10`
Expected: keine Ausgabe.

---

### Task 9: Rhodos-Wächter, Inhalte prüfen, Restnamen bereinigen

**Files:**
- Create: `app/src/test/java/com/example/kosurlaub/NoRhodesLeftoversTest.kt`
- Modify: je nach Treffern des Wächters (`strings.xml`, `TravelContent.kt`, `TravelContentTest.kt`, `CompassContent.kt`, Drawables)

- [ ] **Step 1: Wächter-Test schreiben**

```kotlin
package com.example.kosurlaub

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class NoRhodesLeftoversTest {
    private val banned = Regex(
        "rhod|rodos|rodou|rodiaki|dimokratiki|lindos|kolymbia|faliraki|tsambika|22410|relax hotel|ktel rodou",
        RegexOption.IGNORE_CASE
    )

    @Test
    fun `no Rhodes leftovers in sources and resources`() {
        val roots = listOf("src/main", "src/test", "src/androidTest").map(::File).filter { it.exists() }
        val hits = roots.flatMap { root ->
            root.walkTopDown()
                .filter { it.isFile && it.extension in setOf("kt", "xml") }
                .filter { it.name != "NoRhodesLeftoversTest.kt" }
                .flatMap { file ->
                    file.readLines().withIndex()
                        .filter { banned.containsMatchIn(it.value) }
                        .map { "${file.path}:${it.index + 1}: ${it.value.trim()}" }
                        .toList()
                }
                .toList()
        }
        assertTrue("Rhodos-Reste gefunden:\n" + hits.joinToString("\n"), hits.isEmpty())
    }
}
```

- [ ] **Step 2: Test laufen lassen und Treffer abarbeiten**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "*NoRhodesLeftoversTest" -q 2>&1 | head -40`

Erwartet: Treffer, u. a. `travel_tsambika_*` in `strings.xml`, `TravelContent.kt`, `TravelContentTest.kt`, `excursion_tsambika` (Drawable) und `CompassContent.kt`. Behebung pro Treffer:
1. `tsambika` → `marmari`: in `strings.xml` `travel_tsambika_title/meta/description` zu `travel_marmari_*`, in `TravelContent.kt` Eintrag `id = "marmari"`, `titleRes = R.string.travel_marmari_title` usw., `imageRes = R.drawable.excursion_marmari`, `mapQuery = "Marmari Beach, Kos"`; Datei `excursion_tsambika.webp` → `excursion_marmari.webp` umbenennen; in `TravelContentTest.kt` `"tsambika"` → `"marmari"`.
2. Weitere Treffer: Zeile lesen und durch Kos-Text ersetzen oder löschen. Beispiele: `Relax Hotel` in `InfoCardsUi.kt` (Zeile mit `TimelineItem("~19:00 Uhr", "Check-in Relax Hotel ...")`) → `TimelineItem("~19:00 Uhr", "Check-in ${TripData.HOTEL_NAME} 🏨", "${TripData.ROOM}")`; `Genießt die Sonne im Relax Hotel, das Meer an der Tsambika Bucht ...` in `CountdownUi.kt` → `"Genießt die Sonne in Tigaki, das Meer und fantastisches Essen!"`; Kommentar `<!-- ... RHODOS ... -->` im Widget-Layout → `KOS`; `AB RELAX HOTEL` in `CompassComponents.kt` → `AB HOTEL`.
3. Die Rhodos-Hinweise in Kommentaren (`// ponytail: Die Vorlage (Rhodos) ...`) gehören zu gelöschten Dateien; bleiben welche, Kommentar löschen.

Wiederholen, bis der Test grün ist.

- [ ] **Step 3: Inhalte auf Nicht-Kos prüfen (manuell, eine Liste)**

Diese Stellen auf fremde Orte lesen und gegen die Quelle prüfen; unbelegte Zeilen löschen:
- `strings.xml`: Array `widget_phrases` (Sprüche, ca. 100 Zeilen) – nach Orten außerhalb von Kos und Wörtern wie „Insel Rhodos“ suchen.
- `HomeState.kt`: `kosFactOfTheDay()` (Liste `facts`) – jede Aussage kurz prüfen oder streichen.
- `MainActivity.kt`: `kosHighlightOfTheDay()` – bereits Kos-Bezug; Aussagen zu Öffnungszeiten oder Preisen entfernen.
- `CompassContent.kt`: 14 Tipps – jeder Tipp hat `source = RESEARCHED`; `sourceUrl` muss https sein (Test `researched tips that link out use secure sources`).

Hilfsbefehl: `grep -nE "Insel|Ritter|Akropolis|Lindos|Hafen von" app/src/main/res/values/strings.xml | head -40`

- [ ] **Step 4: Alles grün**

Run: `./gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:compileDebugAndroidTestKotlin -q 2>&1 | tail -10`
Expected: keine Ausgabe.

---

### Task 10: Doku und Bildnachweise

**Files:**
- Create: `NEW/README.md`
- Modify: `NEW/IMAGE_CREDITS.md`

- [ ] **Step 1: `README.md` schreiben**

```markdown
# Kos Urlaub

Private Android-App für den Kos-Urlaub (13.–28.09.2027): Countdown, Widget, Bilder des Tages,
Hotel- und Reiseinfos, Tipps, Wetter, Sprachführer, Tavernen-Rechner, SOS und Bus.

## Bauen und starten

    ./gradlew.bat :app:assembleDebug
    ./gradlew.bat :app:testDebugUnitTest

Debug-Paket: `com.example.kosurlaub.debug`. Reisedaten stehen nur in `TripData.kt`.

## Regeln

- Kein Inhalt ohne Quelle und Stand (Bus, Hotel, Tipps).
- `NoRhodesLeftoversTest` schlägt fehl, wenn Rhodos-Begriffe im Quelltext auftauchen.
```

- [ ] **Step 2: `IMAGE_CREDITS.md` neu schreiben**

Die Datei enthält noch die alten Bildquellen. Ersetzen durch eine Tabelle aller Dateien in `app/src/main/res/drawable-nodpi/` mit Quelle:
- `kos_rot_01` bis `kos_rot_30`: Pexels (Pexels-Lizenz), Seite `https://www.pexels.com/photo/<ID>/`. Zuordnung:

| Datei | Pexels-Foto-ID |
|---|---|
| kos_rot_01 | 697671 |
| kos_rot_02 | 4304017 |
| kos_rot_03 | 5583512 |
| kos_rot_04 | 9903451 |
| kos_rot_05 | 17846666 |
| kos_rot_06 | 12988766 |
| kos_rot_07 | 18130905 |
| kos_rot_08 | 18187079 |
| kos_rot_09 | 17813239 |
| kos_rot_10 | 18187122 |
| kos_rot_11 | 18304713 |
| kos_rot_12 | 17813246 |
| kos_rot_13 | 19497568 |
| kos_rot_14 | 20532563 |
| kos_rot_15 | 17813247 |
| kos_rot_16 | 21067615 |
| kos_rot_17 | 35633151 |
| kos_rot_18 | 28410083 |
| kos_rot_19 | 36022687 |
| kos_rot_20 | 38001871 |
| kos_rot_21 | 28498001 |
| kos_rot_22 | 38001872 |
| kos_rot_23 | 38001870 |
| kos_rot_24 | 36022689 |
| kos_rot_25 | 38001992 |
| kos_rot_26 | 37448697 |
| kos_rot_27 | 20778030 |
| kos_rot_28 | 19914913 |
| kos_rot_29 | 35633150 |
| kos_rot_30 | 36022691 |

- `excursion_kos_stadt`, `excursion_old_town`, `excursion_seven_springs`, `excursion_marmari`: Wikimedia Commons, Urheber und Lizenz wie in `TravelContent.kt` (`imageCredit`, `imageUrl`).
- `category_greek_food`: Pexels, Foto-ID 7282789.
- `hotel_ilios_village`: vtours.com (private Nutzung, Quelle `kgs27364_ilios.k_village_resort_824119_3.jpg`).

- [ ] **Step 3: Prüfen**

Run: `grep -rniE "rhod|rodos|lindos" /c/Claude_Code_Projects/kos-urlaub --include=*.md --include=*.kt --include=*.xml --include=*.kts --exclude-dir=build --exclude-dir=.gradle | grep -v "docs/superpowers" | head`
Expected: keine Treffer.

---

### Task 11: Abnahme auf dem Emulator

**Files:** keine Änderungen, nur Prüfung.

- [ ] **Step 1: Emulator starten (kalt, ohne Snapshot) und App installieren**

```bash
S="$LOCALAPPDATA/Android/Sdk"; ADB="$S/platform-tools/adb.exe"
"$ADB" devices | grep -q emulator || (nohup "$S/emulator/emulator.exe" -avd Pixel_8 -no-snapshot -no-boot-anim -gpu swiftshader_indirect > /tmp/emu.log 2>&1 &)
"$ADB" wait-for-device
until [ "$("$ADB" shell getprop sys.boot_completed | tr -d '\r')" = "1" ]; do sleep 4; done
sleep 20
cd /c/Claude_Code_Projects/kos-urlaub && ./gradlew.bat :app:assembleDebug -q
"$ADB" install -r app/build/outputs/apk/debug/app-debug.apk
MSYS_NO_PATHCONV=1 "$ADB" shell am start -n com.example.kosurlaub.debug/com.example.kosurlaub.MainActivity
```

Expected: `Success` beim Install, `Starting: Intent { ... }` beim Start. Schlägt der Install mit „Broken pipe“ fehl, ist der Emulator noch nicht fertig gestartet: 30 Sekunden warten und erneut installieren.

- [ ] **Step 2: Screenshots und Sichtprüfung**

```bash
SP=/tmp; "$ADB" exec-out screencap -p > $SP/home.png
"$ADB" shell input tap 539 2232; sleep 3; "$ADB" exec-out screencap -p > $SP/compass.png
"$ADB" shell input tap 906 2232; sleep 3; "$ADB" exec-out screencap -p > $SP/travel.png
```

Die drei Bilder ansehen. Soll-Zustand:
- Home: „KOS“, „13.09.2027 · 13:25 Uhr“, Countdown läuft, Tagesbild aus den Kos-Bildern, Navigation mit genau drei Einträgen (Home, Entdecken, Vor Ort & SOS).
- Vor Ort: Tab „Heute“ zeigt die Karte „Reise & Hotel“ mit Flügen, Gepäck und Hotel plus Meer-/Wetterkarte; Tab „Bus“ zeigt die Bus-Karte; Tab „SOS“ zeigt 112, 100, 166, 199 und die Hotel-Nummer mit Hinweis „nicht bestätigt“.
- Nirgends: Relax Hotel, Faliraki, Tsambika, Lindos, Eucalyptus Ave, Rhodos.

- [ ] **Step 3: Widget prüfen**

Widget auf den Startbildschirm ziehen (Widget „Kos 4x2 Groß“) und Screenshot ansehen: Bild, Tage bis Abflug, kein Rhodos-Text.

- [ ] **Step 4: Abschlusslauf**

Run: `cd /c/Claude_Code_Projects/kos-urlaub && ./gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:compileDebugAndroidTestKotlin -q 2>&1 | tail -10`
Expected: keine Ausgabe. Dann `NoRhodesLeftoversTest` einzeln: `./gradlew.bat :app:testDebugUnitTest --tests "*NoRhodesLeftoversTest" -q` → keine Ausgabe.

---

## Offene Punkte (außerhalb dieses Plans)

- Hotel-Straße und bestätigte Telefonnummer (nur Quelle: top-hotels-gr, +30 22420 69411).
- Kos-News als eigener späterer Plan (Cloudflare Worker mit Kos-Quellen und Übersetzung).
- Release-Schlüssel neu erzeugen (der alte `rhodos-release-key.jks` wird nicht übernommen), falls ihr die App später signiert installieren wollt.

## Selbstprüfung gegen die Spezifikation

| Spezifikation | Task |
|---|---|
| Home: Countdown, Widget, Tagesbild, Tagesfakt | 1, 5, 9 |
| Entdecken: 14 Tipps, Merkliste, Foto-Spots Kos | 1, 7, 9 |
| Vor Ort: Hotel, Bus, Sprachführer, Tavernen-Rechner, SOS, Wetter und Meer | 3, 5, 6, 8 |
| Reisedaten nur in `TripData.kt`, Zeitzonen | 4, 5 |
| Wetter für Tigaki | 5 |
| Kein News, kein Updater | 2 |
| Rhodos entfernt, Wächter-Test | 3, 9 |
| Quelle und Stand je Inhalt | 8, 9 |
| Doku und Bildnachweise | 10 |
| Emulator-Abnahme | 11 |

## Notizen

### Task 8: Bus-Recherche

- Quelle: https://www.ktel-kos.gr/ (Unterseite "Γενικά Δρομολόγια": https://ktel-kos.gr/dromologia/), Abruf am 16.10.2026.
- Gefunden: PDF "ΤΙΓΚΑΚΙ" (https://ktel-kos.gr/wp-content/uploads/dromologia/ΤΙΓΚΑΚΙ.pdf) mit Fahrplan Kos - Tigaki und Tigaki - Kos, Mo-Fr, Sa, So, Preis 2,50. Kopf: "VALID FROM: 28/09/2026", kein Enddatum, keine Saisonangabe.
- Einordnung: Die Ankündigung auf der Startseite meldet eine Fahrplanänderung ab 29.06.2026 (Sommerwechsel). Das PDF ab 28.09.2026 ist daher sehr wahrscheinlich der Herbst-/Winterfahrplan. Ob es das ist, steht nicht eindeutig in der Quelle.
- Entscheidung: `routes` bleibt leer (`emptyList()`). Die Karte zeigt "nicht verifiziert" mit Link. Keine Zeiten eingetragen, da kein eindeutig belegter Sommer- oder Sept-2027-Fahrplan vorliegt.
- Offen: Fahrplan für September 2027 ist noch nicht veröffentlicht. Erneut prüfen, sobald KTEL Kos einen Sommer-2027-Fahrplan oder eine Saisonangabe veröffentlicht.
- Nachtrag: Auf Wunsch eingetragen als Referenz (Mo–Sa und Sonntag, je Richtung) mit dem Hinweis „gültig ab 28.09.2026, Saison nicht angegeben; Fahrplan für September 2027 noch nicht veröffentlicht“, Quelle https://ktel-kos.gr/dromologia/, Stand 16.10.2026, Preis 2,50 €. Mo–Fr und Samstag sind im PDF identisch.
