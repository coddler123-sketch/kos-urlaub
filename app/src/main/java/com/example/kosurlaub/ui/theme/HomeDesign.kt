package com.example.kosurlaub.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal val HomeAccent: Color @Composable get() = LocalKosColors.current.accent
internal val HomeCardColor: Color @Composable get() = LocalKosColors.current.card
internal val HomeCardBorder: Color @Composable get() = LocalKosColors.current.cardBorder
internal val HomeCardShape = PostcardShape.Panel

/** Haupttextfarbe (Tinte): hell auf dunklem, dunkel auf hellem Grund. */
internal val KosInk: Color @Composable get() = LocalKosColors.current.ink
internal val KosBg: Color @Composable get() = LocalKosColors.current.bg
internal val KosNav: Color @Composable get() = LocalKosColors.current.nav
internal val KosPanel: Color @Composable get() = LocalKosColors.current.panel
internal val KosPanelHigh: Color @Composable get() = LocalKosColors.current.panelHigh
internal val KosOnAccent: Color @Composable get() = LocalKosColors.current.onAccent
internal val KosOk: Color @Composable get() = LocalKosColors.current.ok
internal val KosWarn: Color @Composable get() = LocalKosColors.current.warn
internal val KosAccent2: Color @Composable get() = LocalKosColors.current.accent2
internal val KosScrim: Color @Composable get() = LocalKosColors.current.scrim
internal val KosStamp: Color @Composable get() = LocalKosColors.current.stamp
internal val KosNumeral: Color @Composable get() = LocalKosColors.current.numeral
