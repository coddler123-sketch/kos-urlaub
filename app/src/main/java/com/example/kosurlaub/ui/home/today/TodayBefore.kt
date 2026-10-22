package com.example.kosurlaub.ui.home.today

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.kosurlaub.ui.home.HomeState
import com.example.kosurlaub.ui.theme.*

/** Heute vor dem Abflug: Klappzahlen-Countdown, Flug und Hotel, gemerkte Tipps, aktuelles Kos-Wetter. */
@Composable
internal fun TodayBefore(
    s: HomeState,
    heroPhoto: ImageBitmap?,
    onOpenTips: () -> Unit
) {
    val context = LocalContext.current
    val flight = TripData.outbound
    val saved = compassTips.filter { it.id in CompassPreferences.saved(context) }
    Column(verticalArrangement = Arrangement.spacedBy(PostcardSpace.Gap)) {
        Box(modifier = Modifier.fillMaxWidth().height(240.dp).clip(PostcardShape.Panel)) {
            if (heroPhoto != null) {
                Image(heroPhoto, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Image(painterResource(s.backgroundRes), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Box(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(110.dp)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))))
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()
                    .padding(start = PostcardSpace.Panel, end = PostcardSpace.Panel, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FlipDigits(s.days)
                    StampChip(stringResource(R.string.widget_days_label))
                }
                Row(
                    Modifier.fillMaxWidth().clip(PostcardShape.Chip).background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "%02d : %02d : %02d".format(s.hours, s.minutes, s.seconds),
                        style = PostcardType.Numeral.copy(fontSize = 22.sp, fontFeatureSettings = "tnum"), color = Color.White
                    )
                    Text(stringResource(R.string.today_before_hms_label), style = PostcardType.Label, color = Color.White.copy(alpha = 0.7f))
                }
            }
            PerforationEdge(modifier = Modifier.align(Alignment.BottomCenter))
        }
        PhraseCard(s.phrase)
        if (s.days == 1L) {
            PaperPanel {
                PostcardLabel(stringResource(R.string.today_before_soon_label))
                Text(
                    stringResource(R.string.today_before_soon_body, TripData.CHECKED_BAG_KG, TripData.CARRY_ON_KG),
                    style = PostcardType.Body, color = KosInk
                )
            }
        }
        BoardingPass(
            flight,
            stringResource(R.string.today_before_flight_hotel),
            "${flight.weekday} %02d.%02d. · ${stringResource(R.string.today_before_hotel)}".format(flight.day, flight.month),
            "${TripData.HOTEL_NAME} · ${TripData.HOTEL_PLACE}"
        )
        CheckInCard()
        if (saved.isNotEmpty()) {
            PaperPanel(modifier = Modifier.clickable(onClick = onOpenTips)) {
                PostcardLabel("${stringResource(R.string.today_before_saved)} · ${saved.size}")
                saved.take(4).forEach { Text("• ${it.title}", style = PostcardType.Body, color = KosInk) }
            }
        }
    }
}
