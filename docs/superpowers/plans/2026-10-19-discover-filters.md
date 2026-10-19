# Entdecken: Filter aufräumen Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Auf der Seite Entdecken > Tipps gibt es nur noch eine Chip-Zeile (Kategorien) plus einen Knopf „Filter“ mit gruppiertem Sheet; die Tipps erscheinen als Foto-Karten im 2-Spalten-Raster wie im Mockup; der Umschalter Tipps | Reise nutzt Postkarten-Farben.

**Architecture:** Neues Designsystem-Bauteil `PostcardChip` (ink-gefüllt wenn gewählt). Neue Datei `ui/compass/CompassBrowse.kt` mit Filter-Gruppen, `CompassFilterSheet`, `CompassChipRow`, `CompassTipPhotoCard`. `CompassScreen` wechselt von `LazyColumn` auf `LazyVerticalGrid` (2 Spalten, Kopf- und Info-Elemente über volle Breite, Tipp-Karten mit `key = tip.id`). Filterlogik (`filterCompassTips`, `matchesCompassQuickFilter`) und Zustand (`selectedCategory`, `selectedFilter`, `searchQuery`) bleiben unverändert.

**Tech Stack:** Kotlin, Compose Material 3 (`ModalBottomSheet`, `FlowRow`, `LazyVerticalGrid`), Designsystem `ui/theme/Postcard*`, JUnit4, Compose-UI-Tests.

Befund und Begründung: `docs/TODO.md`, Abschnitt „Befund Entdecken-Filter“. Mockup: `docs/mockups/app-konzept.html` (dritter Bildschirm).

## Entscheidungen

- Titel bleibt „Ideen & Orte“ (sonst doppelt mit dem Tab „Entdecken“); der Satz „Wähle ein Thema …“ entfällt (es gibt keine Themen-Karten mehr). Die Zeile „20 AUSGEWÄHLTE TIPPS“ bleibt.
- Aus den Quick-Filtern fallen zwei Doppelungen weg: „Essen“ (= Kategorie Essen) und „Rund ums Hotel“ (≈ Ort Tigaki). Die Logik in `matchesCompassQuickFilter` bleibt unverändert (Unit-Test bleibt gültig).
- Quick-Filter bleiben Einzelauswahl wie bisher, nur in vier Gruppen: Ort, Anfahrt, Dauer, Stimmung. Emojis in den Beschriftungen entfallen (die internen Schlüssel behalten sie).
- Die Hero-Karte „Für euch empfohlen“ entfällt (Heute zeigt den Tipp des Tages); `featuredCompassTip` bleibt wegen seines Tests.
- Die Kategorie-Foto-Karten entfallen; die Tipp-Karten selbst sind jetzt das Foto-Raster. Tagesplan-Karte und Community-Karte bleiben.
- Schrift in Chips mindestens 12sp, Innenabstand mindestens 8dp.
- Die Reise-Seite (`TravelScreen`, eigene Unter-Reiter) bleibt unberührt und kommt in Teilprojekt 3.

## Dateistruktur

| Datei | Aufgabe |
|---|---|
| `ui/theme/PostcardComponents.kt` (ändern) | `PostcardChip` |
| `ui/compass/CompassBrowse.kt` (neu) | `quickFilterGroups`, `CompassFilterSheet`, `CompassChipRow`, `CompassTipPhotoCard` |
| `ui/compass/CompassComponents.kt` (ändern) | `CompassOverviewHeader` neu; veraltete Composables löschen; `tipPainter` auf `internal` |
| `ui/compass/CompassUi.kt` (ändern) | `LazyVerticalGrid`, Filter-Sheet, Aufräumen |
| `MainActivity.kt` (ändern) | Umschalter in `DiscoverScreen` mit `PostcardChip` |
| `test/.../CompassQuickFilterTest.kt` (neu) | Filtergruppen prüfen |
| `androidTest/.../MainActivitySmokeTest.kt` (ändern) | Tags und Texte der neuen Seite |

---

### Task 1: `PostcardChip`

**Files:**
- Modify: `app/src/main/java/com/example/kosurlaub/ui/theme/PostcardComponents.kt`

- [ ] **Step 1: Komponente ergänzen** (fehlende Importe ergänzen: `androidx.compose.foundation.selection.selectable`, `androidx.compose.ui.semantics.Role`, `androidx.compose.foundation.shape.RoundedCornerShape`, `FontWeight`)

```kotlin
/** Auswahl-Chip: gewählt = Tinte gefüllt mit Papierschrift, sonst weiße Karte mit Rand. */
@Composable
fun PostcardChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    Text(
        text = label,
        style = PostcardType.Body.copy(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold),
        color = if (selected) KosBg else KosInk,
        maxLines = 1,
        modifier = modifier
            .clip(shape)
            .background(if (selected) KosInk else HomeCardColor)
            .border(1.dp, if (selected) KosInk else HomeCardBorder, shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = PostcardMetrics.CHIP_V_PADDING_DP.dp)
    )
}
```

- [ ] **Step 2: Bauen**

Run: `./gradlew.bat :app:assembleDebug --console=plain -q`
Expected: grün.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/kosurlaub/ui/theme/PostcardComponents.kt
git commit -m "Add PostcardChip to design system"
```

---

### Task 2: Filtergruppen, Sheet, Chip-Zeile, Foto-Karte

**Files:**
- Create: `app/src/main/java/com/example/kosurlaub/ui/compass/CompassBrowse.kt`
- Modify: `app/src/main/java/com/example/kosurlaub/ui/compass/CompassComponents.kt` (`tipPainter` von `private` auf `internal`)
- Test: `app/src/test/java/com/example/kosurlaub/CompassQuickFilterTest.kt`

- [ ] **Step 1: Failing Test**

```kotlin
package com.example.kosurlaub

import com.example.kosurlaub.ui.compass.compassTips
import com.example.kosurlaub.ui.compass.matchesCompassQuickFilter
import com.example.kosurlaub.ui.compass.quickFilterGroups
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CompassQuickFilterTest {

    private val filters = quickFilterGroups.flatMap { it.filters }

    @Test
    fun everyFilterMatchesAtLeastOneTip() {
        filters.forEach { filter ->
            assertTrue("${filter.label} trifft keinen Tipp", compassTips.any { matchesCompassQuickFilter(it, filter.key) })
        }
    }

    @Test
    fun keysAreUnique() {
        assertEquals(filters.size, filters.map { it.key }.toSet().size)
    }

    @Test
    fun labelsHaveNoEmojiAndNoDuplicatesOfCategories() {
        filters.forEach { assertTrue(it.label, it.label.none { c -> Character.getType(c) == Character.OTHER_SYMBOL.toInt() }) }
        assertTrue(filters.none { it.label == "Essen" || it.label == "Rund ums Hotel" })
    }
}
```

- [ ] **Step 2: Fehlschlag prüfen**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "com.example.kosurlaub.CompassQuickFilterTest" --console=plain -q`
Expected: FAIL (Compile-Fehler, `quickFilterGroups` fehlt).

- [ ] **Step 3: `CompassBrowse.kt` schreiben**

```kotlin
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
        LazyRow(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        Image(
            painter = tipPainter(tip, editorial.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().testTag("compass-tip-image-${tip.id}-card")
        )
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))))
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(PostcardMetrics.PHOTO_TEXT_PADDING_DP.dp)
        ) {
            Text(
                tip.title,
                style = PostcardType.Heading.copy(fontSize = 14.sp, lineHeight = 18.sp),
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
            Text(if (isSaved) "♥" else "♡", color = Color.White, fontSize = 22.sp)
        }
    }
}
```

In `CompassComponents.kt`: `private fun tipPainter` → `internal fun tipPainter`.

- [ ] **Step 4: Erfolg prüfen**

Run: `./gradlew.bat :app:testDebugUnitTest --console=plain -q`
Expected: PASS (alle Tests, auch `CompassTipsTest`). Falls `everyFilterMatchesAtLeastOneTip` für einen Filter fehlschlägt, den Filter aus `quickFilterGroups` entfernen und im Bericht nennen (toter Filter).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/kosurlaub/ui/compass/CompassBrowse.kt app/src/main/java/com/example/kosurlaub/ui/compass/CompassComponents.kt app/src/test/java/com/example/kosurlaub/CompassQuickFilterTest.kt
git commit -m "Add grouped quick filters, filter sheet and tip photo cards"
```

---

### Task 3: `CompassScreen` auf Raster umbauen, Kopf, Umschalter

**Files:**
- Modify: `app/src/main/java/com/example/kosurlaub/ui/compass/CompassUi.kt`
- Modify: `app/src/main/java/com/example/kosurlaub/ui/compass/CompassComponents.kt`
- Modify: `app/src/main/java/com/example/kosurlaub/MainActivity.kt` (`DiscoverScreen`)

- [ ] **Step 1: Kopf neu** (`CompassOverviewHeader` in `CompassComponents.kt` ersetzen)

```kotlin
@Composable
internal fun CompassOverviewHeader(count: Int, onRandomTip: () -> Unit = {}) {
    Row(verticalAlignment = Alignment.Bottom) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Ideen & Orte", style = PostcardType.Title, color = KosInk)
            PostcardLabel("$count ausgewählte Tipps")
        }
        PostcardChip("Überrascht uns!", false, onRandomTip, Modifier.testTag("compass-random-tip-button"))
    }
}
```

- [ ] **Step 2: Suchfeld einfärben**

In `CompassSearchField` alle `HomeAccent` in `OutlinedTextFieldDefaults.colors(...)` und im Löschen-Kreuz durch `KosInk` ersetzen (Fokus-Rand, Label, Cursor, `×`).

- [ ] **Step 3: Hauptlayout in `CompassUi.kt`**

Ersetze `LazyColumn(...) { ... }` (alles von `item(key = "header")` bis zum Ende der `if (isOverview) … else …`-Verzweigung) durch ein Raster. Neue Zustände oben im Screen: `var showFilter by rememberSaveable { mutableStateOf(false) }`; `listState` wird `val gridState = rememberLazyGridState()` (alle bisherigen `listState.animateScrollToItem(0)` auf `gridState` umstellen). Entferne `featuredTip`.

```kotlin
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
```

Danach aufräumen, **nur was durch diese Änderung verwaist ist**: in `CompassComponents.kt` die Composables `CompassHeroCard`, `CompassCategoryCard`, `CompassQuickFilters`, `CompassCategoryFilters`, `CompassCategoryFilterChip` löschen; `categoryColor` nur löschen, wenn `grep -rn "categoryColor" app/src` keine Treffer außerhalb der Definition liefert; ungenutzte Importe entfernen. `CompassCategory` bleibt (Felder `description`/`imageRes` bleiben ungenutzt bestehen, im Bericht erwähnen). `featuredCompassTip` bleibt (Unit-Test).

- [ ] **Step 4: Umschalter in `DiscoverScreen`** (`MainActivity.kt`): die beiden `FilterChip` ersetzen:

```kotlin
            listOf("TIPS" to R.string.discover_tab_tips, "TRAVEL" to R.string.discover_tab_travel).forEach { (key, label) ->
                PostcardChip(
                    label = stringResource(label),
                    selected = tab == key,
                    onClick = { onTab(key) },
                    modifier = Modifier.testTag("discover-tab-${key.lowercase()}")
                )
            }
```
Nicht mehr genutzte Importe (`FilterChip`) entfernen.

- [ ] **Step 5: Bauen und Unit-Tests**

Run: `./gradlew.bat :app:assembleDebug :app:testDebugUnitTest --console=plain -q`
Expected: grün.

- [ ] **Step 6: Commit**

```bash
git add -A app/src
git commit -m "Rebuild Entdecken tips as photo grid with one chip row and filter sheet"
```

---

### Task 4: Instrumentierte Tests anpassen

**Files:**
- Modify: `app/src/androidTest/java/com/example/kosurlaub/MainActivitySmokeTest.kt`

- [ ] **Step 1: Anpassungen**

- `compass-category-Unterkünfte` → `compass-category-filter-Unterkünfte` (die Kategorie-Karte gibt es nicht mehr; das Chip-Tag ersetzt sie).
- In `excursionTipCloseReturnsToTipsOverview`: die Zeile `performScrollToKey("Strände")` löschen; `compass-category-Ausflüge` → `compass-category-filter-Ausflüge`.
- In `compassSearchAndSavedTipPersistAfterRecreation`: die Prüfung `onNodeWithText("♥ GEMERKT")` ersetzen durch `composeRule.onNodeWithContentDescription("Tipp gemerkt, tippen zum Entfernen").assertIsDisplayed()` (Import `onNodeWithContentDescription`). Das `performScrollTo()` vor dem Klick auf `compass-save-beach-marmari` bleibt.
- Neuer Test:

```kotlin
    @Test
    fun filterSheetNarrowsTipsAndResets() {
        composeRule.onNodeWithTag("main-nav-discover").performClick()
        composeRule.onNodeWithTag("compass-filter-button").performClick()
        composeRule.onNodeWithTag("compass-filter-sheet").assertIsDisplayed()
        composeRule.onNodeWithTag("compass-filter-Ruhig").performClick()
        composeRule.onNodeWithTag("compass-filter-done").performClick()
        composeRule.onNodeWithText("Filter · 1").assertIsDisplayed()
        composeRule.onNodeWithTag("compass-category-filter-all").performClick()
        composeRule.onNodeWithText("Filter · 1").assertIsDisplayed()
    }
```
(`Alle` setzt nur die Kategorie zurück; der Quick-Filter bleibt, bis er im Sheet zurückgesetzt oder per Zurück-Taste gelöscht wird.)

- [ ] **Step 2: Kompilieren**

Run: `./gradlew.bat :app:compileDebugAndroidTestKotlin --console=plain -q`
Expected: grün.

- [ ] **Step 3: Commit**

```bash
git add app/src/androidTest
git commit -m "Update smoke tests for new Entdecken filters"
```

---

### Task 5: Emulator-Prüfung und Abschluss

- [ ] **Step 1:** Emulator `Pixel_8` (nur `-s emulator-5554`; ein echtes Handy hängt per USB, dort nichts anfassen). Vor jedem Testlauf `adb -s emulator-5554 shell pm clear com.example.kosurlaub.debug`. Debug-APK installieren, Entdecken öffnen, Screenshots in Hell und Dunkel: Umschalter (Ink-Farbe, kein Lavendel), eine Chip-Zeile plus Knopf „Filter“, Foto-Raster in 2 Spalten, Herz tippen (Karte bleibt), Filter-Sheet mit vier Gruppen, Filter „Ruhig“ wählen (Knopf zeigt „Filter · 1“, Trefferzahl), Zurück-Taste löscht Filter, Tipp antippen öffnet Detail, Schließen führt zurück.
- [ ] **Step 2:** `ANDROID_SERIAL=emulator-5554 ./gradlew.bat :app:connectedDebugAndroidTest --console=plain -q` (nach `pm clear`). Fehlschläge genau melden; Text-/Tag-Anpassungen im Test sind erlaubt, Abschwächen der Aussage nicht.
- [ ] **Step 3:** `docs/TODO.md`: Abschnitt „Befund Entdecken-Filter“ als erledigt kennzeichnen (Quick-Filter, Foto-Raster, Umschalter); offen bleibt die Reise-Seite (Teilprojekt 3).
- [ ] **Step 4:** `./gradlew.bat :app:testDebugUnitTest :app:assembleRelease --console=plain -q` grün, committen, pushen. Release nach Rückfrage beim Nutzer.

---

## Selbstprüfung gegen den Befund

- Dreifach-Filter: eine Chip-Zeile (Task 2, 3); Kategorie-Karten entfallen (Task 3).
- Doppelungen und Mischung: Gruppen Ort/Anfahrt/Dauer/Stimmung ohne „Essen“ und „Rund ums Hotel“ (Task 2).
- Nur 3–4 Filter sichtbar: Sheet zeigt alle (Task 2).
- 10sp und Emojis: Chips 13sp, Labels ohne Emoji (Task 1, 2).
- Lavendel-Umschalter: `PostcardChip` (Task 3, Step 4).
- Kopf gestapelt: Titel, Zeile „N ausgewählte Tipps“, Knopf „Überrascht uns!“, Hero entfällt (Task 3).
- Foto-Raster wie Mockup: Task 2/3.
