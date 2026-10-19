# Navigation und Phasen-Startseite Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Drei Tabs (Heute · Entdecken · Tagebuch) und eine Startseite „Heute“, die je nach `TripPhase` (vorher / auf Kos / danach) anders aufgebaut ist, im Postkarten-Look.

**Architecture:** `MainDestination` wird `TODAY, DISCOVER, DIARY`. „Entdecken“ hostet vorerst die bestehenden Screens `CompassScreen` (Tab „Tipps“) und `TravelScreen` (Tab „Reise“) hinter einer Umschaltzeile; die Zusammenlegung folgt in Teilprojekt 3. Die Home-Logik (`KosHome` in `MainActivity.kt`: Laden, Updates, Einstellungen, Tagesleiste) bleibt; nur der Inhalt, wenn kein Reisetag gewählt ist, wird je Phase durch neue Composables in `ui/home/today/` ersetzt. Neue Logik (nächster Bus, Tipp des Tages, Tag-Label) ist rein und unit-getestet.

**Tech Stack:** Kotlin, Jetpack Compose (Material 3), Designsystem aus Teilprojekt 1 (`ui/theme/Postcard*`), JUnit4, Emulator-Sichtprüfung mit dem Debug-Testdatum.

Spec: `docs/superpowers/specs/2026-10-18-app-konzept-design.md` (Teilprojekt 2).
Mockup: `docs/mockups/app-konzept.html`.

## Entscheidungen (mit dem Nutzer abgestimmt)

- **Tagesplan** („Heute vor“) zeigt in diesem Teilprojekt die **gemerkten Tipps ohne Uhrzeit**. Die Zuordnung zu Tagen und Zeiten kommt in Teilprojekt 3.
- **Tipp des Tages** wird **nach Wetter** gewählt (Wind, Hitze, Regen), sonst abwechselnd; Kategorie-Auswahl aus den vorhandenen Tipp-Kategorien.
- **Nächster Bus** nutzt `KtelKosTimetable` und zeigt immer den Hinweis „Referenzfahrplan, Stand <asOf>“ (Projektregel: kein Inhalt ohne Quelle und Stand; der Fahrplan für September 2027 ist noch nicht veröffentlicht).
- Keine erfundenen Klima-Aussagen („Kos im September: 28°“): Die Vorher-Seite zeigt das **aktuelle Wetter aus dem Cache** statt eines Mockup-Texts.
- Schrift nie unter 11sp, Innenabstand mindestens 8dp (Vorgabe Teilprojekt 1).

## Dateistruktur

| Datei | Aufgabe |
|---|---|
| `ui/travel/NextBus.kt` (neu) | Reine Funktion `nextBus(...)` |
| `ui/compass/TipOfTheDay.kt` (neu) | Reine Funktion `tipOfTheDay(...)` |
| `ui/home/TripPhase.kt` (ändern) | `TripDays.label(...)`: „TAG 4/16“ |
| `ui/theme/PostcardComponents.kt` (ändern) | `FlipDigits` |
| `ui/home/today/TodayBefore.kt` (neu) | Seite vor dem Abflug |
| `ui/home/today/TodayOnKos.kt` (neu) | Seite auf Kos |
| `ui/home/today/TodayAfter.kt` (neu) | Seite nach dem Urlaub |
| `MainActivity.kt` (ändern) | 3 Tabs, `DiscoverScreen`, Dispatch in `KosHome` |
| `res/values/strings.xml` (ändern) | Tab-Texte, neue Texte |
| `androidTest/.../MainActivitySmokeTest.kt` (ändern) | Neue Texte der Heute-Seite |
| Tests: `NextBusTest.kt`, `TipOfTheDayTest.kt`, `TripDayLabelTest.kt` (neu) | Logik |

Bestehende Teile bleiben: `HeaderSection`, `CountdownProgress`, `FreeChecklistCard`, `HomeQuickActions`, `FactCard`/`HighlightCard`, `BeachWindRadarCard`, `BottomSection`, `DayStrip`, `DayPage`, Einstellungen und Update-Banner.

---

### Task 1: Nächster Bus (reine Logik)

**Files:**
- Create: `app/src/main/java/com/example/kosurlaub/ui/travel/NextBus.kt`
- Test: `app/src/test/java/com/example/kosurlaub/NextBusTest.kt`

- [ ] **Step 1: Failing Test**

```kotlin
package com.example.kosurlaub

import com.example.kosurlaub.ui.travel.BusDeparture
import com.example.kosurlaub.ui.travel.BusRoute
import com.example.kosurlaub.ui.travel.nextBus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class NextBusTest {

    private fun route(validity: String, vararg times: String) = BusRoute(
        from = "Tigaki", to = "Kos-Stadt", validity = validity,
        departures = times.map { BusDeparture(it) }, source = "test", asOf = "01.01.2026"
    )

    private val routes = listOf(
        route("Mo–Sa · Referenz", "08:10", "15:30", "22:15"),
        route("Sonntag · Referenz", "10:15", "18:00")
    )

    private fun at(year: Int, month: Int, day: Int, hour: Int, minute: Int) =
        Calendar.getInstance(TimeZone.getTimeZone("Europe/Athens")).apply {
            clear(); set(year, month - 1, day, hour, minute)
        }

    @Test
    fun picksNextDepartureOnWeekday() {
        // 16.09.2027 ist ein Donnerstag
        val bus = nextBus("Tigaki", "Kos-Stadt", at(2027, 9, 16, 15, 0), routes)!!
        assertEquals("15:30", bus.time)
        assertEquals(30, bus.minutesUntil)
    }

    @Test
    fun usesSundayTimetableOnSunday() {
        // 19.09.2027 ist ein Sonntag
        val bus = nextBus("Tigaki", "Kos-Stadt", at(2027, 9, 19, 9, 0), routes)!!
        assertEquals("10:15", bus.time)
    }

    @Test
    fun returnsNullAfterLastDeparture() {
        assertNull(nextBus("Tigaki", "Kos-Stadt", at(2027, 9, 16, 23, 0), routes))
    }

    @Test
    fun returnsNullForUnknownRoute() {
        assertNull(nextBus("Tigaki", "Mastichari", at(2027, 9, 16, 9, 0), routes))
    }
}
```

- [ ] **Step 2: Fehlschlag prüfen**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "com.example.kosurlaub.NextBusTest" --console=plain -q`
Expected: FAIL (Compile-Fehler, `nextBus` fehlt).

- [ ] **Step 3: Implementieren**

```kotlin
package com.example.kosurlaub.ui.travel

import java.util.Calendar

internal data class NextBus(val time: String, val minutesUntil: Int, val route: BusRoute)

/**
 * Nächste Abfahrt von [from] nach [to] ab [now] (Ortszeit Kos), oder null, wenn heute keine mehr fährt
 * oder die Strecke fehlt. Sonntags gilt die Sonntags-Tabelle, sonst "Mo–Sa".
 */
internal fun nextBus(
    from: String,
    to: String,
    now: Calendar,
    routes: List<BusRoute> = KtelKosTimetable.routes
): NextBus? {
    val prefix = if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) "Sonntag" else "Mo–Sa"
    val route = routes.firstOrNull { it.from == from && it.to == to && it.validity.startsWith(prefix) } ?: return null
    val nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
    val time = route.departures.map { it.time }.firstOrNull { minutesOf(it) >= nowMinutes } ?: return null
    return NextBus(time, minutesOf(time) - nowMinutes, route)
}

private fun minutesOf(time: String): Int =
    time.substringBefore(":").toInt() * 60 + time.substringAfter(":").toInt()
```

- [ ] **Step 4: Erfolg prüfen**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "com.example.kosurlaub.NextBusTest" --console=plain -q`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/kosurlaub/ui/travel/NextBus.kt app/src/test/java/com/example/kosurlaub/NextBusTest.kt
git commit -m "Add next bus lookup from KTEL timetable"
```

---

### Task 2: Tipp des Tages und Tag-Label (reine Logik)

**Files:**
- Create: `app/src/main/java/com/example/kosurlaub/ui/compass/TipOfTheDay.kt`
- Modify: `app/src/main/java/com/example/kosurlaub/ui/home/TripPhase.kt` (in `object TripDays` ergänzen)
- Test: `app/src/test/java/com/example/kosurlaub/TipOfTheDayTest.kt`, `app/src/test/java/com/example/kosurlaub/TripDayLabelTest.kt`

- [ ] **Step 1: Failing Tests**

`TipOfTheDayTest.kt`:

```kotlin
package com.example.kosurlaub

import com.example.kosurlaub.ui.compass.CompassTip
import com.example.kosurlaub.ui.compass.tipOfTheDay
import com.example.kosurlaub.ui.weather.KosWeather
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TipOfTheDayTest {

    private fun tip(id: String, category: String) =
        CompassTip(category = category, title = id, description = "", note = "", id = id)

    private val tips = listOf(
        tip("trip-a", "Ausflüge"), tip("trip-b", "Ausflüge"),
        tip("beach-a", "Strände"), tip("food-a", "Essen"),
        tip("hotel", "Unterkünfte"), tip("bus", "Mobilität")
    )

    private fun weather(wind: Int = 10, temp: Int = 25, rain: Double = 0.0) = KosWeather(
        temperatureCelsius = temp, apparentTemperatureCelsius = temp, relativeHumidityPercent = 50,
        precipitationMm = rain, windSpeedKmh = wind, weatherCode = 0, isDay = true
    )

    @Test
    fun windyDayPicksExcursion() {
        assertEquals("Ausflüge", tipOfTheDay(tips, weather(wind = 35), 0)!!.tip.category)
    }

    @Test
    fun hotDryDayPicksBeach() {
        assertEquals("Strände", tipOfTheDay(tips, weather(temp = 31), 0)!!.tip.category)
    }

    @Test
    fun rainyDayPicksFood() {
        assertEquals("Essen", tipOfTheDay(tips, weather(rain = 2.0), 0)!!.tip.category)
    }

    @Test
    fun sameDaySameTipDifferentDayRotates() {
        val w = weather(wind = 35)
        assertEquals(tipOfTheDay(tips, w, 5)!!.tip.id, tipOfTheDay(tips, w, 5)!!.tip.id)
        assertEquals(setOf("trip-a", "trip-b"), (0L..1L).map { tipOfTheDay(tips, w, it)!!.tip.id }.toSet())
    }

    @Test
    fun neverPicksHotelOrMobility() {
        (0L..20L).forEach { day ->
            val category = tipOfTheDay(tips, null, day)!!.tip.category
            assertEquals(true, category in setOf("Ausflüge", "Strände", "Essen"))
        }
    }

    @Test
    fun emptyListGivesNull() {
        assertNull(tipOfTheDay(emptyList(), null, 0))
    }
}
```

`TripDayLabelTest.kt`:

```kotlin
package com.example.kosurlaub

import com.example.kosurlaub.ui.home.TripDays
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class TripDayLabelTest {

    private fun millis(year: Int, month: Int, day: Int) =
        Calendar.getInstance(TimeZone.getTimeZone("Europe/Athens")).apply {
            clear(); set(year, month - 1, day, 12, 0)
        }.timeInMillis

    @Test
    fun labelsDayWithinTrip() {
        assertEquals("TAG 4/16", TripDays.label(millis(2027, 9, 16)))
    }

    @Test
    fun noLabelOutsideTrip() {
        assertNull(TripDays.label(millis(2027, 10, 5)))
    }
}
```

- [ ] **Step 2: Fehlschlag prüfen**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "com.example.kosurlaub.TipOfTheDayTest" --tests "com.example.kosurlaub.TripDayLabelTest" --console=plain -q`
Expected: FAIL (Symbole fehlen).

- [ ] **Step 3: Implementieren**

`TipOfTheDay.kt`:

```kotlin
package com.example.kosurlaub.ui.compass

import com.example.kosurlaub.ui.weather.KosWeather

internal data class TipOfTheDay(val tip: CompassTip, val reason: String)

private const val WINDY_KMH = 30
private const val HOT_CELSIUS = 28
private const val RAIN_MM = 0.5
private val rotation = listOf("Ausflüge", "Strände", "Essen")

/**
 * Ein Tipp pro Tag, nach Wetter gewählt: Wind → Ausflug, Hitze ohne Regen → Strand, Regen → Essen,
 * sonst Kategorien im Wechsel. Nur Empfehlungen aus Ausflügen, Stränden und Essen; stabil je [epochDay].
 */
internal fun tipOfTheDay(tips: List<CompassTip>, weather: KosWeather?, epochDay: Long): TipOfTheDay? {
    val (category, reason) = when {
        weather != null && weather.precipitationMm >= RAIN_MM -> "Essen" to "Regenpause: Zeit für eine Taverne."
        weather != null && weather.windSpeedKmh >= WINDY_KMH -> "Ausflüge" to "Windtag: passt zu einem Ausflug."
        weather != null && weather.temperatureCelsius >= HOT_CELSIUS -> "Strände" to "Heißer, trockener Tag: ab ans Wasser."
        else -> rotation[(epochDay % rotation.size).toInt()] to "Heute vorgeschlagen."
    }
    val candidates = tips.filter { it.kind == CompassTipKind.RECOMMENDATION && it.category == category }
        .ifEmpty { tips.filter { it.kind == CompassTipKind.RECOMMENDATION && it.category in rotation } }
    if (candidates.isEmpty()) return null
    return TipOfTheDay(candidates[(epochDay % candidates.size).toInt()], reason)
}
```

In `TripPhase.kt`, in `object TripDays` nach `fun today(...)` ergänzen:

```kotlin
    /** "TAG 4/16" für den laufenden Reisetag, sonst null. */
    fun label(nowMillis: Long): String? = today(nowMillis)?.let { "TAG ${it.index}/${all.size}" }
```

- [ ] **Step 4: Erfolg prüfen**

Run: `./gradlew.bat :app:testDebugUnitTest --console=plain -q`
Expected: PASS (alle Tests).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/kosurlaub/ui/compass/TipOfTheDay.kt app/src/main/java/com/example/kosurlaub/ui/home/TripPhase.kt app/src/test/java/com/example/kosurlaub/TipOfTheDayTest.kt app/src/test/java/com/example/kosurlaub/TripDayLabelTest.kt
git commit -m "Add weather-based tip of the day and trip day label"
```

---

### Task 3: Navigation mit drei Tabs und Entdecken-Container

**Files:**
- Modify: `app/src/main/java/com/example/kosurlaub/MainActivity.kt` (Zeilen ca. 126–256: `MainDestination`, `KosApp`, `MainNavigationBar`)
- Modify: `app/src/main/res/values/strings.xml` (Zeilen 3–6)

- [ ] **Step 1: Strings**

Ersetze in `strings.xml` die vier `main_nav_*`-Zeilen durch:

```xml
    <string name="main_nav_today">Heute</string>
    <string name="main_nav_discover">Entdecken</string>
    <string name="main_nav_diary">Tagebuch</string>
    <string name="discover_tab_tips">Tipps</string>
    <string name="discover_tab_travel">Reise</string>
```

- [ ] **Step 2: Enum und Navigationsleiste**

```kotlin
private enum class MainDestination { TODAY, DISCOVER, DIARY }
```

In `MainNavigationBar`: `labelRes` → `TODAY -> R.string.main_nav_today`, `DISCOVER -> R.string.main_nav_discover`, `DIARY -> R.string.main_nav_diary`; `iconRes` → `TODAY -> R.drawable.ic_nav_home`, `DISCOVER -> R.drawable.ic_nav_compass`, `DIARY -> R.drawable.ic_nav_diary`. Der Test-Tag `main-nav-${destination.name.lowercase()}` bleibt (ergibt `main-nav-today` usw.).

- [ ] **Step 3: `KosApp` umbauen**

Ersetze `destinationName`-Start, `BackHandler`, Tagesleiste und das `when (destination)` so:

```kotlin
    val destinationName = rememberSaveable {
        mutableStateOf(if (startInTravel) MainDestination.DISCOVER.name else MainDestination.TODAY.name)
    }
    val discoverTab = rememberSaveable { mutableStateOf(if (startInTravel) "TRAVEL" else "TIPS") }
    // ... (übrige States unverändert)
    val destination = MainDestination.valueOf(destinationName.value)

    BackHandler(enabled = !nestedDetailVisible.value && destination != MainDestination.TODAY) {
        destinationName.value = MainDestination.TODAY.name
    }
```

In `bottomBar`: `if (destination == MainDestination.TODAY) { DayStrip(...) }`.

Im `when`:

```kotlin
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
            MainDestination.DIARY -> DiaryScreen(/* unverändert */)
```

Die alten Zweige `TRAVEL` und `COMPASS` entfallen.

- [ ] **Step 4: `DiscoverScreen` ergänzen** (in `MainActivity.kt` unter `MainNavigationBar`)

```kotlin
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
                FilterChip(
                    selected = tab == key,
                    onClick = { onTab(key) },
                    label = { Text(stringResource(label)) },
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
```

Fehlende Imports (`FilterChip`, `Row`, `Arrangement`, `Box`) ergänzen. Falls `ic_nav_travel` und `main_nav_*`-Reste danach ungenutzt sind (`grep -rn "ic_nav_travel" app/src`), die Ressource löschen (von dieser Änderung verwaist).

- [ ] **Step 5: Bauen und prüfen**

Run: `./gradlew.bat :app:assembleDebug :app:testDebugUnitTest --console=plain -q`
Expected: grün. Dann `grep -rn "MainDestination\.\(HOME\|COMPASS\|TRAVEL\)\|main-nav-home\|main-nav-compass\|main-nav-travel" app/src` → keine Treffer (androidTest-Referenzen anpassen, falls vorhanden).

- [ ] **Step 6: Commit**

```bash
git add -A app/src
git commit -m "Switch to three tabs: Heute, Entdecken, Tagebuch"
```

---

### Task 4: `FlipDigits` im Designsystem

**Files:**
- Modify: `app/src/main/java/com/example/kosurlaub/ui/theme/PostcardComponents.kt`

- [ ] **Step 1: Komponente ergänzen** (Imports `Arrangement`, `Row`, `RoundedCornerShape`, `sp`, `semantics`, `contentDescription` bei Bedarf ergänzen)

```kotlin
/** Klappzahlen wie auf der Abflugtafel: drei Kacheln, Tage mit führenden Nullen. */
@Composable
fun FlipDigits(value: Long, modifier: Modifier = Modifier) {
    val digits = value.coerceIn(0, 999).toString().padStart(3, '0')
    Row(
        modifier = modifier.semantics(mergeDescendants = true) { contentDescription = "$value" },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        digits.forEach { digit ->
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF2C3733), Color(0xFF111815)))),
                contentAlignment = Alignment.Center
            ) {
                Text(digit.toString(), style = PostcardType.Numeral.copy(fontSize = 40.sp), color = Color(0xFFF6F0E4))
                Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x99000000)))
            }
        }
    }
}
```

- [ ] **Step 2: Bauen**

Run: `./gradlew.bat :app:assembleDebug --console=plain -q`
Expected: grün.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/kosurlaub/ui/theme/PostcardComponents.kt
git commit -m "Add FlipDigits component"
```

---

### Task 5: Heute – vor dem Abflug

**Files:**
- Create: `app/src/main/java/com/example/kosurlaub/ui/home/today/TodayBefore.kt`
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Strings**

```xml
    <string name="today_before_flight_hotel">Flug &amp; Hotel</string>
    <string name="today_before_saved">Gemerkte Tipps</string>
    <string name="today_before_saved_empty">Noch nichts gemerkt. Schau bei Entdecken vorbei.</string>
    <string name="today_before_weather">Kos jetzt</string>
    <string name="today_open_ideas">Entdecken</string>
```

- [ ] **Step 2: Composable**

```kotlin
package com.example.kosurlaub.ui.home.today

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.kosurlaub.R
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.compass.CompassPreferences
import com.example.kosurlaub.ui.compass.compassTips
import com.example.kosurlaub.ui.home.CountdownProgress
import com.example.kosurlaub.ui.home.HomeState
import com.example.kosurlaub.ui.theme.*
import java.util.Locale

/** Heute vor dem Abflug: Klappzahlen-Countdown, Flug und Hotel, gemerkte Tipps, aktuelles Kos-Wetter. */
@Composable
internal fun TodayBefore(
    s: HomeState,
    heroPhoto: ImageBitmap?,
    onOpenIdeas: () -> Unit
) {
    val context = LocalContext.current
    val flight = TripData.outbound
    val saved = compassTips.filter { it.id in CompassPreferences.saved(context) }
    Column(verticalArrangement = Arrangement.spacedBy(PostcardSpace.Gap)) {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(PostcardShape.Panel)) {
            if (heroPhoto != null) {
                Image(heroPhoto, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Image(painterResource(s.backgroundRes), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Row(
                modifier = Modifier.align(Alignment.TopStart).padding(PostcardSpace.Panel),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FlipDigits(s.days)
                StampChip(stringResource(R.string.widget_days_label))
            }
            s.weather?.let {
                StampBadge(top = it.temperatureLabel, bottom = "KOS", modifier = Modifier.align(Alignment.TopEnd).padding(PostcardSpace.Panel))
            }
            PerforationEdge(modifier = Modifier.align(Alignment.BottomCenter))
        }
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                "${flight.fromCity} → ${flight.toCity}".uppercase(Locale.GERMAN),
                style = PostcardType.Heading, color = KosInk
            )
            Text(
                "${s.hours.toString().padStart(2, '0')} ${stringResource(R.string.widget_hours_label)}  ${s.minutes.toString().padStart(2, '0')} ${stringResource(R.string.widget_minutes_label)}",
                style = PostcardType.Label, color = KosInk
            )
        }
        CountdownProgress(s.progress)
        Text(s.phrase, style = PostcardType.Quote, color = KosInk)

        PaperPanel {
            PostcardLabel(stringResource(R.string.today_before_flight_hotel))
            Text(
                "${flight.weekday} %02d.%02d. · %02d:%02d %s → %02d:%02d %s".format(
                    flight.day, flight.month, flight.departHour, flight.departMinute, flight.fromAirport,
                    flight.arriveHour, flight.arriveMinute, flight.toAirport
                ),
                style = PostcardType.Body, color = KosInk
            )
            Text("${TripData.HOTEL_NAME} · ${TripData.HOTEL_PLACE}", style = PostcardType.Body, color = KosInk)
        }
        PaperPanel(modifier = Modifier.clickable(onClick = onOpenIdeas)) {
            PostcardLabel("${stringResource(R.string.today_before_saved)} · ${saved.size}")
            if (saved.isEmpty()) {
                Text(stringResource(R.string.today_before_saved_empty), style = PostcardType.Body, color = KosInk)
            } else {
                saved.take(4).forEach { Text("• ${it.title}", style = PostcardType.Body, color = KosInk) }
            }
        }
        s.weather?.let { w ->
            PaperPanel {
                PostcardLabel(stringResource(R.string.today_before_weather))
                Text("${w.temperatureLabel} · ${w.apparentTemperatureLabel} · Wind ${w.windSpeedLabel}", style = PostcardType.Body, color = KosInk)
            }
        }
    }
}
```

Hinweis: `PerforationEdge` malt in `KosBg`; liegt darunter Foto, sieht man Zähne als Papier-Zacken. Auf dem Hero liegt die Kante am unteren Rand des Fotos über dem Seitenhintergrund (`KosBg`), das passt.

- [ ] **Step 3: Bauen**

Run: `./gradlew.bat :app:assembleDebug --console=plain -q`
Expected: grün (noch nicht eingebunden, kompiliert aber).

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/example/kosurlaub/ui/home/today/TodayBefore.kt app/src/main/res/values/strings.xml
git commit -m "Add Heute page for the time before departure"
```

---

### Task 6: Heute – auf Kos

**Files:**
- Create: `app/src/main/java/com/example/kosurlaub/ui/home/today/TodayOnKos.kt`
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Strings**

```xml
    <string name="today_greeting">Kalimera!</string>
    <string name="today_air">Luft</string>
    <string name="today_sea">Meer</string>
    <string name="today_wind">Wind</string>
    <string name="today_sunset">Sonne ↓</string>
    <string name="today_plan">Heute vor</string>
    <string name="today_plan_empty">Noch nichts gemerkt. Tipps findest du bei Entdecken.</string>
    <string name="today_next_bus">Nächster Bus</string>
    <string name="today_bus_none">Heute keine Abfahrt mehr</string>
    <string name="today_bus_source">Referenzfahrplan, Stand %1$s</string>
    <string name="today_tip">Tipp des Tages</string>
    <string name="today_sos">SOS</string>
```

- [ ] **Step 2: Composable**

```kotlin
package com.example.kosurlaub.ui.home.today

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.kosurlaub.R
import com.example.kosurlaub.ui.compass.CompassPreferences
import com.example.kosurlaub.ui.compass.compassTips
import com.example.kosurlaub.ui.compass.tipOfTheDay
import com.example.kosurlaub.ui.home.AppClock
import com.example.kosurlaub.ui.home.HomeState
import com.example.kosurlaub.ui.home.TripDays
import com.example.kosurlaub.ui.home.localEpochDay
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.ui.travel.nextBus
import com.example.kosurlaub.ui.weather.KosWeather
import com.example.kosurlaub.ui.weather.MarineWeatherRepository
import com.example.kosurlaub.ui.weather.WeatherRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/** Heute auf Kos: Wetter und Meer, Tagesplan (gemerkte Tipps), nächster Bus, Tipp des Tages. */
@Composable
internal fun TodayOnKos(
    s: HomeState,
    heroPhoto: ImageBitmap?,
    onOpenIdeas: () -> Unit,
    onOpenSos: () -> Unit
) {
    val context = LocalContext.current
    val now = AppClock.nowMillis()
    val athens = TimeZone.getTimeZone("Europe/Athens")
    val kosNow = Calendar.getInstance(athens).apply { timeInMillis = now }
    val date = SimpleDateFormat("EEEE, d. MMMM", Locale.GERMAN).apply { timeZone = athens }.format(kosNow.time)
    val saved = compassTips.filter { it.id in CompassPreferences.saved(context) }
    val rawWeather: KosWeather? = WeatherRepository.cached(context)
    val sea = MarineWeatherRepository.cached(context)?.seaSurfaceTemperatureCelsius
    val bus = nextBus("Tigaki", "Kos-Stadt", kosNow)
    val tip = tipOfTheDay(compassTips, rawWeather, localEpochDay())

    Box {
        Column(verticalArrangement = Arrangement.spacedBy(PostcardSpace.Gap)) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).clip(PostcardShape.Panel)) {
                if (heroPhoto != null) {
                    Image(heroPhoto, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Image(painterResource(s.backgroundRes), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))))
                Column(Modifier.align(Alignment.BottomStart).padding(PostcardSpace.Panel)) {
                    Text(stringResource(R.string.today_greeting), style = PostcardType.Title, color = Color.White)
                    Text(date.uppercase(Locale.GERMAN), style = PostcardType.Label, color = Color.White)
                }
                TripDays.label(now)?.let { label ->
                    val (top, bottom) = label.split(" ").let { it[0] to it[1] }
                    StampBadge(top = top, bottom = bottom, modifier = Modifier.align(Alignment.BottomEnd).padding(PostcardSpace.Panel))
                }
                PerforationEdge(modifier = Modifier.align(Alignment.BottomCenter))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Fact(stringResource(R.string.today_air), s.weather?.temperatureLabel ?: "–")
                Fact(stringResource(R.string.today_sea), sea?.let { "${Math.round(it)}°" } ?: "–")
                Fact(stringResource(R.string.today_wind), s.weather?.windSpeedLabel ?: "–")
                Fact(stringResource(R.string.today_sunset), s.sunsetLabel?.substringBefore(" Uhr") ?: "–")
            }
            PaperPanel(modifier = Modifier.clickable(onClick = onOpenIdeas)) {
                PostcardLabel(stringResource(R.string.today_plan))
                if (saved.isEmpty()) {
                    Text(stringResource(R.string.today_plan_empty), style = PostcardType.Body, color = KosInk)
                } else {
                    saved.take(4).forEach { Text("• ${it.title}", style = PostcardType.Body, color = KosInk) }
                }
            }
            PaperPanel {
                PostcardLabel(stringResource(R.string.today_next_bus))
                if (bus == null) {
                    Text(stringResource(R.string.today_bus_none), style = PostcardType.Body, color = KosInk)
                } else {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Tigaki → Kos-Stadt · ${bus.time}", style = PostcardType.Body, color = KosInk)
                        Text("${bus.minutesUntil} Min", style = PostcardType.Numeral, color = KosStamp)
                    }
                    Text(stringResource(R.string.today_bus_source, bus.route.asOf), style = PostcardType.Label, color = KosInk.copy(alpha = 0.8f))
                }
            }
            tip?.let {
                PaperPanel(modifier = Modifier.clickable(onClick = onOpenIdeas)) {
                    PostcardLabel(stringResource(R.string.today_tip))
                    Text(it.tip.title, style = PostcardType.Heading, color = KosInk)
                    Text(it.reason, style = PostcardType.Body, color = KosInk)
                }
            }
        }
        Text(
            text = stringResource(R.string.today_sos),
            style = PostcardType.Label,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(PostcardSpace.Panel)
                .size(48.dp)
                .clip(CircleShape)
                .background(KosStamp)
                .clickable(onClick = onOpenSos)
                .wrapContentSize(Alignment.Center)
                .testTag("today-sos")
        )
    }
}

@Composable
private fun Fact(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = PostcardType.Numeral.copy(fontSize = PostcardType.Heading.fontSize), color = KosInk)
        PostcardLabel(label)
    }
}
```

Hinweise: Der SOS-Knopf liegt im scrollenden Inhalt unten rechts, nicht fest am Bildschirm. Fester Knopf folgt, wenn er sich in `KosHome` außerhalb des Scroll-Containers platzieren lässt (siehe Task 8, Step 2). Die Marine-Temperatur kann `null` sein, dann zeigt die Zeile „–“.

- [ ] **Step 3: Bauen**

Run: `./gradlew.bat :app:assembleDebug --console=plain -q`
Expected: grün. Falls ein Import-Name nicht passt (z. B. `MarineWeatherRepository.cached`), per `grep` den echten Namen suchen und minimal anpassen.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/example/kosurlaub/ui/home/today/TodayOnKos.kt app/src/main/res/values/strings.xml
git commit -m "Add Heute page for the time on Kos"
```

---

### Task 7: Heute – nach dem Urlaub

**Files:**
- Create: `app/src/main/java/com/example/kosurlaub/ui/home/today/TodayAfter.kt`
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Strings**

```xml
    <string name="today_after_title">Schön war es.</string>
    <string name="today_after_days">Tage</string>
    <string name="today_after_visited">Tipps besucht</string>
    <string name="today_after_entries">Einträge</string>
```

- [ ] **Step 2: Composable** (Zahlen wie `DiaryScreen`: `diaryStats`, `DayData.load` je Tag)

Prüfe zuerst in `DiaryScreen.kt`, wie die Einträge je Tag geladen werden (`entriesByDay`), und nutze denselben Aufruf.

```kotlin
package com.example.kosurlaub.ui.home.today

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.kosurlaub.R
import com.example.kosurlaub.ui.compass.CompassPreferences
import com.example.kosurlaub.ui.home.HomeState
import com.example.kosurlaub.ui.home.PolaroidPhoto
import com.example.kosurlaub.ui.home.TripDays
import com.example.kosurlaub.ui.theme.*

/** Heute nach dem Urlaub: Rückblick mit Foto und Zahlen. */
@Composable
internal fun TodayAfter(s: HomeState, heroPhoto: ImageBitmap?, entriesCount: Int) {
    val context = LocalContext.current
    val visited = CompassPreferences.visited(context).size
    Column(verticalArrangement = Arrangement.spacedBy(PostcardSpace.Gap), horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.today_after_title), style = PostcardType.Title, color = KosInk)
        PolaroidPhoto(imageRes = s.backgroundRes, caption = "Kos", photo = heroPhoto)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Stat(TripDays.all.size.toString(), stringResource(R.string.today_after_days))
            Stat(visited.toString(), stringResource(R.string.today_after_visited))
            Stat(entriesCount.toString(), stringResource(R.string.today_after_entries))
        }
    }
}

@Composable
private fun Stat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = PostcardType.Numeral, color = KosNumeral)
        PostcardLabel(label)
    }
}
```

`entriesCount` wird in Task 8 aus den gespeicherten Tageseinträgen berechnet (`TripDays.all.sumOf { DayData.load(context, it.index).size }`, mit dem tatsächlichen Objektnamen aus `ui/home/DayData.kt`).

- [ ] **Step 3: Bauen und Commit**

Run: `./gradlew.bat :app:assembleDebug --console=plain -q` → grün.

```bash
git add app/src/main/java/com/example/kosurlaub/ui/home/today/TodayAfter.kt app/src/main/res/values/strings.xml
git commit -m "Add Heute page for the time after the trip"
```

---

### Task 8: In `KosHome` einbinden und Smoke-Test anpassen

**Files:**
- Modify: `app/src/main/java/com/example/kosurlaub/MainActivity.kt` (`KosHome`, ca. Zeilen 355–411)
- Modify: `app/src/androidTest/java/com/example/kosurlaub/MainActivitySmokeTest.kt`

- [ ] **Step 1: Phase-Dispatch**

Im `else`-Zweig von `KosHome` (kein Reisetag gewählt) ersetze `PolaroidPhoto(...)` bis einschließlich `CountdownSection(s)` durch:

```kotlin
            val phase = TripPhase.of(AppClock.nowMillis())
            val privatePath = remember(PrivateImages.version.intValue, s.backgroundRes) { Images.privatePathOfTheDay(context) }
            val heroPhoto = remember(privatePath) { privatePath?.let { PrivateImages.bitmap(context, it, 1200)?.asImageBitmap() } }
            when (phase) {
                TripPhase.BEFORE -> TodayBefore(s, heroPhoto, onOpenIdeas)
                TripPhase.ON_KOS -> TodayOnKos(s, heroPhoto, onOpenIdeas, onOpenSos = { onOpenTravel("HELP") })
                TripPhase.AFTER -> TodayAfter(
                    s, heroPhoto,
                    entriesCount = TripDays.all.sumOf { DayData.load(context, it.index).size }
                )
            }
```

Die folgenden bestehenden Blöcke bleiben je Phase: `FlightDayTimelineCard` (nur Reisetag), `FreeChecklistCard` und `FactCard`/`HighlightCard` nur bei `BEFORE`; `HomeQuickActions` nur bei `ON_KOS`; `BeachWindRadarCard` und `BottomSection` bei `BEFORE` und `ON_KOS`. Der Block `val privatePath … PolaroidPhoto … CountdownSection` entfällt (jetzt in den Today-Seiten). Imports ergänzen (`TodayBefore`, `TodayOnKos`, `TodayAfter`, `TripPhase`, `DayData`).

- [ ] **Step 2: SOS fest am Bildschirm (optional, nur wenn einfach)**

Wenn der Knopf aus `TodayOnKos` im Scroll-Inhalt untergeht, entferne ihn dort und platziere ihn in `KosHome` im äußeren `Box` (außerhalb der `verticalScroll`-Column) mit `Modifier.align(Alignment.BottomEnd)` und `onClick = { onOpenTravel("HELP") }`, nur bei `phase == TripPhase.ON_KOS`.

- [ ] **Step 3: Smoke-Test anpassen**

In `MainActivitySmokeTest.kt` die Erwartungen der Heute-Seite auf die neuen Texte setzen: `"TAGE"`, `"STD."`, `"MIN."` bleiben (Chip und Label in `TodayBefore`); `"SEK."` und `"Bis zu unserem Kos-Urlaub"` entfallen oder werden durch `"Flug & Hotel"` ersetzt; `home-checklist` und der Fakt-/Tipp-Text bleiben (`FreeChecklistCard`, `FactCard`). Tab-Tags: `main-nav-today`, `main-nav-discover`, `main-nav-diary`.

- [ ] **Step 4: Bauen, Unit-Tests**

Run: `./gradlew.bat :app:assembleDebug :app:testDebugUnitTest --console=plain -q`
Expected: grün.

- [ ] **Step 5: Commit**

```bash
git add -A app/src
git commit -m "Wire phase-based Heute pages into home and update smoke test"
```

---

### Task 9: Emulator-Sichtprüfung in allen Phasen

**Files:** keine (nur Prüfung; Reparaturen als Commit)

- [ ] **Step 1: Emulator** (`Pixel_8`, nur `-s emulator-5554`; ein echtes Handy hängt per USB, dort nichts installieren). Debug-APK installieren, App öffnen.

- [ ] **Step 2: Phasen durchschalten.** In Einstellungen (Zahnrad) → Debug „TESTDATUM“: „Echte Zeit“ (vorher), „Auf Kos (16.09.)“, „Nach Rückflug (29.09.)“. Je Phase Screenshot im Hell- und Dunkelmodus (`adb -s emulator-5554 shell cmd uimode night yes|no`).

Prüfen: Vorher: Klappzahlen, Perforation, Flug/Hotel, gemerkte Tipps, Wetter. Kos: „Kalimera!“, TAG-Stempel, Luft/Meer/Wind/Sonne, Tagesplan, Bus mit „Referenzfahrplan, Stand …“, Tipp des Tages, SOS erreichbar und öffnet Entdecken → Reise → Hilfe. Danach: Rückblick mit Zahlen. Tabs: Heute, Entdecken (Umschalter Tipps/Reise), Tagebuch; Zurück-Taste führt zu Heute.

- [ ] **Step 3: Instrumentierter Smoke-Test** (falls Emulator läuft)

Run: `./gradlew.bat :app:connectedDebugAndroidTest --console=plain -q` (nur gegen den Emulator: `ANDROID_SERIAL=emulator-5554` setzen).
Expected: grün; sonst Texte im Test anpassen.

- [ ] **Step 4: Reparaturen committen**, falls nötig.

---

### Task 10: Abschluss

- [ ] **Step 1:** In `docs/TODO.md` Teilprojekt 2 abhaken; in der Spec die drei offenen Fragen als entschieden markieren (Tagesplan = gemerkte Tipps, Tipp des Tages = Wetter, Rückblick = Foto plus drei Zahlen).
- [ ] **Step 2:** `./gradlew.bat :app:testDebugUnitTest :app:assembleRelease --console=plain -q` → grün, dann Commit und Push.
- [ ] **Step 3:** Release als Patch (sichtbare Änderung) nach Rückfrage beim Nutzer.

---

## Selbstprüfung gegen die Spec

- Tabs Heute · Entdecken · Tagebuch: Task 3.
- Vorher (Abflugtafel-Held, Flug und Hotel, gemerkte Tipps, Wetter): Task 4, 5. Der „Wetter-Trend“ aus der Spec ist hier das aktuelle Cache-Wetter (keine Klimaaussagen ohne Quelle).
- Auf Kos (Wetter und Meer, Tagesplan, Bus, Tipp des Tages, SOS): Task 1, 2, 6, 8.
- Danach (Rückblick mit Zahlen und Foto): Task 7.
- Nicht in diesem Teilprojekt: Zusammenlegung Kompass und Reise (Teilprojekt 3), Tageszuordnung der Tipps (Teilprojekt 3), Tagebuch-Optik (Teilprojekt 4).
