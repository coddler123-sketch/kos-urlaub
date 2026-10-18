package com.example.kosurlaub.ui.home
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun CountdownProgress(fraction: Float) {
    Column(modifier = Modifier.padding(top = 10.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "VORFREUDE BIS ZUM ABFLUG",
                color = KosInk.copy(alpha = 0.75f),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = KosSans,
                letterSpacing = 0.6.sp
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${(fraction * 100).roundToInt()} %",
                color = KosInk.copy(alpha = 0.75f),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = KosSans
            )
        }
        Spacer(Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(2.dp))
                    .background(KosInk.copy(alpha = 0.2f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(HomeAccent)
            )
        }
    }
}

@Composable
fun CountdownSection(s: HomeState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        if (s.isOver) {
            TripOverCard()
        } else if (s.isOnVacation) {
            LiveVacationCompanionCard(s = s)
        } else if (s.isReached) {
            Text(
                text = "Es ist soweit!",
                color = KosInk,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = KosSerif
            )
        } else {
            CappedFontScale(maxScale = 1.15f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CountdownBlock(s.days.toString(), "TAGE")
                    CountdownDivider()
                    CountdownBlock(s.hours.toString().padStart(2, '0'), "STD.")
                    CountdownDivider()
                    CountdownBlock(s.minutes.toString().padStart(2, '0'), "MIN.")
                    CountdownDivider()
                    CountdownBlock(s.seconds.toString().padStart(2, '0'), "SEK.", secondary = true)
                }
            }
            countdownMilestone(s.days)?.let { milestone ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .clip(HomeCardShape)
                            .background(HomeAccent.copy(alpha = 0.18f))
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            milestone.title,
                            color = HomeAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = KosSans,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            milestone.message,
                            color = KosInk,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = KosSans
                        )
                    }
                }
            }
        }
        if (!s.isOnVacation && !s.isOver) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "„${s.phrase}“",
                color = KosInk,
                fontSize = 17.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = KosSans,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CountdownBlock(value: String, label: String, secondary: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = if (secondary) KosInk.copy(alpha = 0.85f) else KosInk,
            fontSize = if (secondary) 38.sp else 42.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSerif
        )
        Text(
            text = label,
            color = if (secondary) KosInk.copy(alpha = 0.65f) else KosInk.copy(alpha = 0.9f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSans,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun CappedFontScale(maxScale: Float, content: @Composable () -> Unit) {
    val current = LocalDensity.current
    val capped = remember(current.density, current.fontScale, maxScale) {
        Density(
            density = current.density,
            fontScale = min(current.fontScale, maxScale)
        )
    }
    CompositionLocalProvider(LocalDensity provides capped, content = content)
}

@Composable
private fun CountdownDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 11.dp)
            .width(1.dp)
            .height(42.dp)
            .background(KosInk.copy(alpha = 0.4f))
    )
}

@Composable
fun LiveVacationCompanionCard(s: HomeState, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeAccent.copy(alpha = 0.18f))
            .border(1.dp, HomeAccent.copy(alpha = 0.4f), HomeCardShape)
            .padding(20.dp)
    ) {
        Text(
            text = "WILLKOMMEN AUF KOS! 🌊🌴",
            color = HomeAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSans,
            letterSpacing = 0.8.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Euer Urlaub in Tigaki läuft!",
            color = KosInk,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSans,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Genießt die Sonne in Tigaki, das Meer und fantastisches Essen!",
            color = KosInk.copy(alpha = 0.9f),
            fontSize = 13.sp,
            fontFamily = KosSans,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

/** Nach dem Rückflug: kein Countdown mehr, Verweis auf das Tagebuch. */
@Composable
fun TripOverCard(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeAccent.copy(alpha = 0.18f))
            .border(1.dp, HomeAccent.copy(alpha = 0.4f), HomeCardShape)
            .padding(20.dp)
            .testTag("trip-over-card")
    ) {
        Text(
            text = "ZURÜCK ZU HAUSE 🌅",
            color = HomeAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            fontFamily = KosSans, letterSpacing = 0.8.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Schön war's!",
            color = KosInk, fontSize = 26.sp, fontWeight = FontWeight.Bold,
            fontFamily = KosSerif, textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Im Tagebuch steht, was ihr erlebt habt.",
            color = KosInk.copy(alpha = 0.9f), fontSize = 14.sp,
            fontFamily = KosSans, textAlign = TextAlign.Center
        )
    }
}
