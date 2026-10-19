package com.example.kosurlaub

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToKey
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivitySmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearPinnedImageBeforeTest() {
        clearPinnedImage()
        clearCompassPreferences()
    }

    @After
    fun clearPinnedImageAfterTest() {
        clearPinnedImage()
        clearCompassPreferences()
    }

    @Test
    fun mainScreenShowsCoreCountdownContent() {
        composeRule.onAllNodesWithText("KOS").onFirst().assertIsDisplayed()
        val beforeTrip = composeRule.onAllNodesWithText("Bis zu unserem Kos-Urlaub").fetchSemanticsNodes().isNotEmpty()
        composeRule.onNodeWithText(if (beforeTrip) "Bis zu unserem Kos-Urlaub" else "Unser Kos-Urlaub").assertIsDisplayed()
        if (composeRule.onAllNodesWithText("TAGE").fetchSemanticsNodes().isNotEmpty()) {
            // Vor dem Abflug: TodayBefore zeigt TAGE als Stempel, STD./MIN. stehen im selben Text wie die Zahl.
            composeRule.onNodeWithText("TAGE").assertIsDisplayed()
            composeRule.onNodeWithText("STD.", substring = true).assertIsDisplayed()
            composeRule.onNodeWithText("MIN.", substring = true).assertIsDisplayed()
        } else {
            // Auf Kos und danach zeigt die Startseite andere Texte (Begrüßung bzw. Rückblick).
            val phaseTexts = listOf("Kalimera!", "Schön war es.")
            val shown = phaseTexts.first { composeRule.onAllNodesWithText(it).fetchSemanticsNodes().isNotEmpty() }
            composeRule.onNodeWithText(shown).assertIsDisplayed()
        }
        // Checkliste, Kos-Fakt und Highlight gibt es nur vor dem Abflug.
        if (composeRule.onAllNodesWithTag("home-checklist").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithTag("home-checklist").performScrollTo().assertIsDisplayed()
            // Je nach Tag erscheint der Kos-Fakt oder der Highlight-Tipp.
            val highlight = composeRule.onAllNodesWithText("TIPP FÜR EUREN URLAUB").fetchSemanticsNodes().isNotEmpty()
            composeRule.onNodeWithText(if (highlight) "TIPP FÜR EUREN URLAUB" else "KOS-FAKT").performScrollTo().assertIsDisplayed()
        }
    }

    @Test
    fun settingsSheetOpensGallery() {
        composeRule.onNodeWithTag("settings-button").performClick()
        composeRule.onNodeWithText("Optionen").assertIsDisplayed()

        composeRule.onNodeWithTag("settings-open-gallery").performClick()

        composeRule.onNodeWithText("Titelbild wählen").assertIsDisplayed()
        composeRule.onNodeWithTag("gallery-auto-image").assertIsDisplayed()
    }

    @Test
    fun galleryImageSelectionPersistsAfterActivityRecreation() {
        composeRule.onNodeWithTag("settings-button").performClick()
        composeRule.onNodeWithTag("settings-open-gallery").performClick()

        composeRule.onNodeWithTag("gallery-image-kos_rot_01").performClick()
        composeRule.onNodeWithTag("gallery-apply").performClick()

        composeRule.onAllNodesWithText("KOS").onFirst().assertIsDisplayed()
        composeRule.onNodeWithText("Bis zu unserem Kos-Urlaub").assertIsDisplayed()

        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("kos_rot_01", Images.getPinnedImage(context))

        composeRule.activityRule.scenario.recreate()
        composeRule.onNodeWithTag("settings-button").performClick()
        composeRule.onNodeWithTag("settings-open-gallery").performClick()
        composeRule.onNodeWithTag("gallery-image-kos_rot_01").assertIsSelected()
    }

    @Test
    fun compassOpensFromMainNavigationAndReturns() {
        composeRule.onNodeWithTag("main-nav-discover").performClick()
        composeRule.onNodeWithText("Ideen & Orte").assertIsDisplayed()
        composeRule.onNodeWithTag("compass-screen").assertIsDisplayed()
        composeRule.onNodeWithText("${compassTipIds.size} AUSGEWÄHLTE TIPPS").assertIsDisplayed()
        composeRule.onNodeWithTag("compass-screen").performScrollToKey("community")
        composeRule.onNodeWithTag("community-link").assertIsDisplayed()

        composeRule.onNodeWithTag("compass-category-Unterkünfte").performClick()
        composeRule.onNodeWithTag("compass-screen").assertIsDisplayed()
        composeRule.onNodeWithText("1 Treffer").assertIsDisplayed()

        composeRule.onNodeWithTag("compass-category-filter-all").performClick()
        composeRule.onNodeWithText("${compassTipIds.size} AUSGEWÄHLTE TIPPS").assertIsDisplayed()

        pressBack()

        composeRule.onAllNodesWithText("KOS").onFirst().assertIsDisplayed()
    }

    @Test
    fun compassSearchAndSavedTipPersistAfterRecreation() {
        composeRule.onNodeWithTag("main-nav-discover").performClick()
        composeRule.onNodeWithTag("compass-search").performTextInput("Marmari")
        composeRule.onNodeWithTag("compass-tip-beach-marmari").assertIsDisplayed()
        composeRule.onNodeWithTag("compass-tip-image-beach-marmari-card", useUnmergedTree = true)
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("compass-save-beach-marmari").performClick()

        composeRule.activityRule.scenario.recreate()

        composeRule.onNodeWithTag("compass-tip-beach-marmari").assertIsDisplayed()
        composeRule.onNodeWithTag("compass-save-beach-marmari").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("♥ GEMERKT").assertIsDisplayed()
    }

    @Test
    fun hotelTipOpensWithDirectMapLink() {
        composeRule.onNodeWithTag("main-nav-discover").performClick()
        composeRule.onNodeWithTag("compass-search").performTextInput("Ilios")
        composeRule.onNodeWithTag("compass-tip-hotel-ilios-k-village").performClick()

        composeRule.onNodeWithTag("compass-tip-overlay").assertIsDisplayed()
        composeRule.onNodeWithTag("compass-map-hotel-ilios-k-village")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithTag("compass-note-hotel-ilios-k-village")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun excursionTipCloseReturnsToTipsOverview() {
        composeRule.onNodeWithTag("main-nav-discover").performClick()
        composeRule.onNodeWithTag("compass-screen").performScrollToKey("Strände")
        composeRule.onNodeWithTag("compass-category-Ausflüge").performClick()
        composeRule.onNodeWithTag("compass-screen").performScrollToKey("trip-asklepieion")
        composeRule.onNodeWithTag("compass-tip-trip-asklepieion").performClick()
        composeRule.onNodeWithTag("compass-tip-overlay").assertIsDisplayed()
        composeRule.onNodeWithTag("compass-close-trip-asklepieion").assertIsDisplayed().performClick()

        composeRule.onNodeWithTag("compass-screen").assertIsDisplayed()
        composeRule.onNodeWithTag("compass-tip-trip-asklepieion").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("compass-tip-overlay").assertIsDisplayed()

        pressBack()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithTag("compass-tip-overlay").fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithTag("compass-screen").assertIsDisplayed()
        composeRule.onNodeWithTag("compass-tip-trip-asklepieion").assertIsDisplayed()
    }

    @Test
    fun bottomNavigationSwitchesBetweenMainDestinations() {
        composeRule.onNodeWithTag("main-nav-today").assertIsSelected()

        composeRule.onNodeWithTag("main-nav-discover").performClick().assertIsSelected()
        composeRule.onNodeWithTag("compass-screen").assertIsDisplayed()

        composeRule.onNodeWithTag("main-nav-today").performClick().assertIsSelected()
        composeRule.onAllNodesWithText("KOS").onFirst().assertIsDisplayed()
    }

    @Test
    fun tappingActiveTabReturnsToTop() {
        composeRule.onNodeWithTag("main-nav-discover").performClick()
        composeRule.onNodeWithTag("compass-screen").performScrollToIndex(3)

        composeRule.onNodeWithTag("main-nav-discover").performClick()

        composeRule.onNodeWithText("Ideen & Orte").assertIsDisplayed()
    }

    @Test
    fun randomTipButtonOpensDetailOverlay() {
        composeRule.onNodeWithTag("main-nav-discover").performClick()
        composeRule.onNodeWithTag("compass-random-tip-button").performClick()

        composeRule.onNodeWithTag("compass-tip-overlay").assertIsDisplayed()
    }

    @Test
    fun tavernCalculatorCalculatesTotalAndPerPerson() {
        composeRule.onNodeWithTag("main-nav-discover").performClick()
        composeRule.onNodeWithTag("discover-tab-travel").performClick()
        composeRule.onNodeWithTag("travel-area-explore").performClick()

        composeRule.onNodeWithTag("tavern-calculator-amount-input")
            .performScrollTo()
            .performTextInput("50")
        composeRule.onNodeWithText("Gesamt inkl. Trinkgeld:").assertIsDisplayed()
        composeRule.onNodeWithText("55,00 €").assertIsDisplayed()
    }

    private fun pressBack() {
        composeRule.activityRule.scenario.onActivity {
            it.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.waitForIdle()
    }

    private fun clearPinnedImage() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("kos_settings", Context.MODE_PRIVATE)
            .edit()
            .remove("pinned_image_name")
            .commit()
    }

    private fun clearCompassPreferences() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("kos_compass", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }
}
