# Postkarten-Designsystem Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Gemeinsame Postkarten-Bausteine (Schriften, Farben, Abstände, sechs Compose-Komponenten) bereitstellen, ohne eine bestehende Seite zu ändern.

**Architecture:** Neue Dateien im Paket `ui/theme` (Tokens, Komponenten). `KosColors` bekommt zwei zusätzliche Farben. Ein Debug-only-Screen zeigt alle Bausteine zur Sichtprüfung im Emulator. Reine Zahlenwerte stehen in `PostcardMetrics` (ohne Android-Abhängigkeit), damit ein Unit-Test die Vorgaben "Schrift ≥ 11sp, Innenabstand ≥ 8dp" prüfen kann.

**Tech Stack:** Kotlin, Jetpack Compose (Material 3), vorhandene Schriften `KosSerif`/`KosSans`, System-Schmalschrift (`sans-serif-condensed`), JUnit4.

Spec: `docs/superpowers/specs/2026-10-18-app-konzept-design.md`, Teilprojekt 1.
Mockup: `docs/mockups/app-konzept.html`.

---

## Dateistruktur

| Datei | Aufgabe |
|---|---|
| `app/src/main/java/com/example/kosurlaub/ui/theme/PostcardMetrics.kt` (neu) | Reine Zahlenwerte: Schriftgrößen, Abstände, Mindestwerte |
| `app/src/main/java/com/example/kosurlaub/ui/theme/PostcardTokens.kt` (neu) | `KosCondensed`, `PostcardType`, `PostcardSpace`, `PostcardShape` |
| `app/src/main/java/com/example/kosurlaub/ui/theme/KosColors.kt` (ändern) | Felder `stamp` und `numeral` ergänzen |
| `app/src/main/java/com/example/kosurlaub/ui/theme/HomeDesign.kt` (ändern) | Zugriffe `KosStamp`, `KosNumeral` |
| `app/src/main/java/com/example/kosurlaub/ui/theme/PostcardComponents.kt` (neu) | `PostcardLabel`, `StampChip`, `StampBadge`, `PerforationEdge`, `PaperPanel`, `PostcardPhotoCard` |
| `app/src/test/java/com/example/kosurlaub/PostcardMetricsTest.kt` (neu) | Prüft Mindestgrößen |
| `app/src/debug/AndroidManifest.xml` (neu) | Debug-Activity registrieren |
| `app/src/debug/java/com/example/kosurlaub/DesignGalleryActivity.kt` (neu) | Zeigt alle Bausteine, nur im Debug-Build |

Keine bestehende Seite wird angefasst. Bestehende Zugriffe wie `KosInk`, `HomeCardColor` bleiben unverändert.

---

### Task 1: Zahlenwerte und Test

**Files:**
- Create: `app/src/main/java/com/example/kosurlaub/ui/theme/PostcardMetrics.kt`
- Test: `app/src/test/java/com/example/kosurlaub/PostcardMetricsTest.kt`

- [ ] **Step 1: Failing Test schreiben**

```kotlin
package com.example.kosurlaub

import com.example.kosurlaub.ui.theme.PostcardMetrics
import org.junit.Assert.assertTrue
import org.junit.Test

class PostcardMetricsTest {

    @Test
    fun noTextStyleIsBelowMinimum() {
        PostcardMetrics.textSizesSp.forEach { (name, size) ->
            assertTrue("$name: $size sp < ${PostcardMetrics.MIN_TEXT_SP} sp", size >= PostcardMetrics.MIN_TEXT_SP)
        }
    }

    @Test
    fun noInnerPaddingIsBelowMinimum() {
        PostcardMetrics.innerPaddingsDp.forEach { (name, size) ->
            assertTrue("$name: $size dp < ${PostcardMetrics.MIN_PADDING_DP} dp", size >= PostcardMetrics.MIN_PADDING_DP)
        }
    }
}
```

- [ ] **Step 2: Test laufen lassen, Fehlschlag prüfen**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "com.example.kosurlaub.PostcardMetricsTest" --console=plain -q`
Expected: FAIL (Compile-Fehler, `PostcardMetrics` fehlt).

- [ ] **Step 3: Minimal implementieren**

```kotlin
package com.example.kosurlaub.ui.theme

/** Reine Zahlenwerte des Postkarten-Designsystems; ohne Android-Abhängigkeit, damit sie unit-testbar sind. */
object PostcardMetrics {
    const val MIN_TEXT_SP = 11f
    const val MIN_PADDING_DP = 8f

    const val TITLE_SP = 26f
    const val HEADING_SP = 18f
    const val QUOTE_SP = 14f
    const val BODY_SP = 14f
    const val LABEL_SP = 11f
    const val NUMERAL_SP = 28f

    const val PANEL_PADDING_DP = 12f
    const val CHIP_H_PADDING_DP = 8f
    const val CHIP_V_PADDING_DP = 8f
    const val PHOTO_TEXT_PADDING_DP = 10f

    val textSizesSp = mapOf(
        "Title" to TITLE_SP, "Heading" to HEADING_SP, "Quote" to QUOTE_SP,
        "Body" to BODY_SP, "Label" to LABEL_SP, "Numeral" to NUMERAL_SP
    )

    val innerPaddingsDp = mapOf(
        "Panel" to PANEL_PADDING_DP, "ChipH" to CHIP_H_PADDING_DP,
        "ChipV" to CHIP_V_PADDING_DP, "PhotoText" to PHOTO_TEXT_PADDING_DP
    )
}
```

- [ ] **Step 4: Test laufen lassen, Erfolg prüfen**

Run: `./gradlew.bat :app:testDebugUnitTest --tests "com.example.kosurlaub.PostcardMetricsTest" --console=plain -q`
Expected: PASS (keine Ausgabe, Exit 0).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/kosurlaub/ui/theme/PostcardMetrics.kt app/src/test/java/com/example/kosurlaub/PostcardMetricsTest.kt
git commit -m "Add postcard design metrics with minimum size test"
```

---

### Task 2: Tokens (Schriften, Abstände, Formen) und Farben

**Files:**
- Create: `app/src/main/java/com/example/kosurlaub/ui/theme/PostcardTokens.kt`
- Modify: `app/src/main/java/com/example/kosurlaub/ui/theme/KosColors.kt`
- Modify: `app/src/main/java/com/example/kosurlaub/ui/theme/HomeDesign.kt`

- [ ] **Step 1: Farben ergänzen**

In `KosColors.kt` zur Klasse `KosColors` nach `val scrim: Color` ergänzen (Komma nach `scrim` setzen):

```kotlin
    val scrim: Color,
    val stamp: Color,
    val numeral: Color
```

`DarkKosColors` nach `scrim = Color.Black`:

```kotlin
    scrim = Color.Black,
    stamp = Color(0xFFE0735F),
    numeral = Color(0xFF7FC4DA)
```

`LightKosColors` nach `scrim = Color(0xFFF6F0E4)`:

```kotlin
    scrim = Color(0xFFF6F0E4),
    stamp = Color(0xFFB8432F),
    numeral = Color(0xFF1F5F7A)
```

In `HomeDesign.kt` am Ende ergänzen:

```kotlin
internal val KosStamp: Color @Composable get() = LocalKosColors.current.stamp
internal val KosNumeral: Color @Composable get() = LocalKosColors.current.numeral
```

- [ ] **Step 2: Tokens schreiben**

```kotlin
package com.example.kosurlaub.ui.theme

import android.graphics.Typeface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Schmalschrift für Zahlen und Etiketten; wie in den Widgets die System-Schmalschrift. */
val KosCondensed = FontFamily(Typeface.create("sans-serif-condensed", Typeface.BOLD))

/** Textstile des Postkarten-Looks; Größen stehen in [PostcardMetrics]. Farbe setzt der Aufrufer. */
object PostcardType {
    val Title = TextStyle(
        fontFamily = KosSerif, fontWeight = FontWeight.Bold,
        fontSize = PostcardMetrics.TITLE_SP.sp, lineHeight = 32.sp
    )
    val Heading = TextStyle(
        fontFamily = KosSerif, fontWeight = FontWeight.Bold,
        fontSize = PostcardMetrics.HEADING_SP.sp, lineHeight = 24.sp
    )
    val Quote = TextStyle(
        fontFamily = KosSerif, fontStyle = FontStyle.Italic,
        fontSize = PostcardMetrics.QUOTE_SP.sp, lineHeight = 20.sp
    )
    val Body = TextStyle(
        fontFamily = KosSans, fontWeight = FontWeight.Normal,
        fontSize = PostcardMetrics.BODY_SP.sp, lineHeight = 20.sp
    )
    val Label = TextStyle(
        fontFamily = KosCondensed, fontWeight = FontWeight.Bold,
        fontSize = PostcardMetrics.LABEL_SP.sp, letterSpacing = 1.6.sp
    )
    val Numeral = TextStyle(
        fontFamily = KosCondensed, fontWeight = FontWeight.Bold,
        fontSize = PostcardMetrics.NUMERAL_SP.sp
    )
}

object PostcardSpace {
    val Screen = 16.dp
    val Gap = 10.dp
    val Panel = PostcardMetrics.PANEL_PADDING_DP.dp
}

object PostcardShape {
    val Panel = RoundedCornerShape(16.dp)
    val Photo = RoundedCornerShape(14.dp)
    val Chip = RoundedCornerShape(4.dp)
}
```

- [ ] **Step 3: Bauen**

Run: `./gradlew.bat :app:assembleDebug --console=plain -q`
Expected: BUILD SUCCESSFUL, keine Ausgabe.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/example/kosurlaub/ui/theme/
git commit -m "Add postcard tokens and stamp/numeral colors"
```

---

### Task 3: Komponenten

**Files:**
- Create: `app/src/main/java/com/example/kosurlaub/ui/theme/PostcardComponents.kt`

- [ ] **Step 1: Komponenten schreiben**

```kotlin
package com.example.kosurlaub.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Kleines Großbuchstaben-Etikett über Karten (z. B. "FLUG & HOTEL"). */
@Composable
fun PostcardLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = PostcardType.Label,
        color = KosInk.copy(alpha = 0.8f),
        modifier = modifier
    )
}

/** Rechteckiger Stempel-Chip, z. B. "TAGE". */
@Composable
fun StampChip(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = PostcardType.Label,
        color = KosInk,
        modifier = modifier
            .clip(PostcardShape.Chip)
            .background(KosPanelHigh)
            .padding(
                horizontal = PostcardMetrics.CHIP_H_PADDING_DP.dp,
                vertical = PostcardMetrics.CHIP_V_PADDING_DP.dp
            )
    )
}

/** Runder Stempel mit gestricheltem Rand, z. B. "TAG 4/16" oder Wetter. */
@Composable
fun StampBadge(top: String, bottom: String, modifier: Modifier = Modifier) {
    val color = KosStamp
    Column(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(KosBg.copy(alpha = 0.95f))
            .drawBehind {
                drawCircle(
                    color = color,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 2.dp.toPx()))
                    )
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(top, style = PostcardType.Label, color = color, textAlign = TextAlign.Center)
        Text(bottom, style = PostcardType.Label.copy(fontSize = PostcardType.Heading.fontSize), color = color)
    }
}

/** Gezackte Kante zwischen Foto und Papier. [color] ist die Farbe des Papiers darunter. */
@Composable
fun PerforationEdge(color: Color = KosBg, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(6.dp)) {
        val tooth = 7.dp.toPx()
        val gap = 5.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawRect(color, topLeft = Offset(x, 0f), size = Size(tooth, size.height))
            x += tooth + gap
        }
    }
}

/** Papier-Karte für Inhalte. */
@Composable
fun PaperPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(PostcardShape.Panel)
            .background(HomeCardColor)
            .border(1.dp, HomeCardBorder, PostcardShape.Panel)
            .padding(PostcardSpace.Panel),
        content = content
    )
}

/** Foto-Karte mit Titel und Untertitel unten auf dunklem Verlauf. */
@Composable
fun PostcardPhotoCard(
    @DrawableRes image: Int,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(128.dp).clip(PostcardShape.Photo)) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(PostcardMetrics.PHOTO_TEXT_PADDING_DP.dp)
        ) {
            Text(title, style = PostcardType.Heading.copy(fontSize = PostcardMetrics.BODY_SP.sp), color = Color.White)
            Text(subtitle, style = PostcardType.Label, color = Color.White.copy(alpha = 0.9f))
        }
    }
}
```

- [ ] **Step 2: Bauen**

Run: `./gradlew.bat :app:assembleDebug --console=plain -q`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/example/kosurlaub/ui/theme/PostcardComponents.kt
git commit -m "Add postcard components"
```

---

### Task 4: Debug-Galerie

**Files:**
- Create: `app/src/debug/AndroidManifest.xml`
- Create: `app/src/debug/java/com/example/kosurlaub/DesignGalleryActivity.kt`

- [ ] **Step 1: Debug-Manifest**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <activity
            android:name=".DesignGalleryActivity"
            android:exported="true"
            android:label="Designsystem"
            android:theme="@style/Theme.KosWidget" />
    </application>
</manifest>
```

- [ ] **Step 2: Galerie-Activity**

```kotlin
package com.example.kosurlaub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kosurlaub.ui.theme.*

/** Nur im Debug-Build: zeigt alle Postkarten-Bausteine zur Sichtprüfung. */
class DesignGalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KosWidgetTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(KosBg)
                        .systemBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(PostcardSpace.Screen),
                    verticalArrangement = Arrangement.spacedBy(PostcardSpace.Gap)
                ) {
                    Text("Schön war es.", style = PostcardType.Title, color = KosInk)
                    Text("Sonnenuntergang in Tigaki, die Füße im Wasser.", style = PostcardType.Quote, color = KosInk)
                    Row(horizontalArrangement = Arrangement.spacedBy(PostcardSpace.Gap), verticalAlignment = Alignment.CenterVertically) {
                        Text("330", style = PostcardType.Numeral, color = KosNumeral)
                        StampChip("Tage")
                        StampBadge("TAG", "4/16")
                    }
                    Box { PerforationEdge() }
                    PaperPanel {
                        PostcardLabel("Flug & Hotel")
                        Text("Mo 13.09. · 13:25 HAM → 17:45 KGS", style = PostcardType.Body, color = KosInk)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(PostcardSpace.Gap)) {
                        PostcardPhotoCard(R.drawable.excursion_old_town, "Altstadt", "Ausflug", Modifier.weight(1f))
                        PostcardPhotoCard(R.drawable.excursion_marmari, "Marmari", "Strand", Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 3: Bauen, Tests, Emulator-Sichtprüfung**

Run: `./gradlew.bat :app:assembleDebug :app:testDebugUnitTest --console=plain -q`
Expected: BUILD SUCCESSFUL, Tests grün.

Emulator starten (`Pixel_8`), APK installieren (`adb -s emulator-5554 install -r -t app/build/outputs/apk/debug/app-debug.apk`), dann:
`adb -s emulator-5554 shell am start -n com.example.kosurlaub.debug/com.example.kosurlaub.DesignGalleryActivity`
Screenshot per `adb exec-out screencap -p`. Prüfen: Serife in Titel, Schmalschrift bei Zahl und Etiketten, Stempel mit gestricheltem Rand, Perforation sichtbar, Foto-Karten lesbar. Im Hell- und im Dunkelmodus (`adb shell cmd uimode night yes|no`).

- [ ] **Step 4: Commit**

```bash
git add app/src/debug
git commit -m "Add debug-only design gallery for postcard components"
```

---

### Task 5: Abschluss

- [ ] **Step 1: ToDo abhaken**

In `docs/TODO.md` bei Teilprojekt 1 `- [ ]` zu `- [x]` ändern.

- [ ] **Step 2: Gesamtprüfung und Commit**

Run: `./gradlew.bat :app:testDebugUnitTest :app:assembleRelease --console=plain -q`
Expected: grün. Kein Release nötig, da keine sichtbare Änderung in der Release-App; das Designsystem wird mit Teilprojekt 2 ausgeliefert.

```bash
git add docs/TODO.md
git commit -m "Mark postcard design system as done"
git push origin main
```

---

## Selbstprüfung gegen die Spec

- Farben, Schriften (Serife Titel, Sans/Schmalschrift Zahlen): Task 2.
- Foto-Karte, Stempel-Chip, Papier-Panel, Perforationskante: Task 3 (zusätzlich `StampBadge`, `PostcardLabel`).
- Hell und dunkel: Farben je Palette in `KosColors`, Prüfung beider Modi in Task 4.
- Keine Funktionsänderung: Keine bestehende Datei außer `KosColors`/`HomeDesign` (nur Ergänzungen) wird geändert.
- Mindestschrift 11sp, Mindestabstand 8dp: Task 1 (Test) und Verwendung der Werte in Task 2/3.
