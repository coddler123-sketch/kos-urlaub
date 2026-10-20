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

/** Flug und Hotel als Bordkarte: Codes und Zeiten oben, Abreißkante mit Kerben, Hotel im Abschnitt darunter. */
@Composable
private fun BoardingPass(f: TripData.Flight) {
    val notch = KosBg
    val dash = HomeCardBorder
    Column(
        Modifier.fillMaxWidth().clip(PostcardShape.Panel).background(HomeCardColor)
            .border(1.dp, HomeCardBorder, PostcardShape.Panel)
    ) {
        Column(Modifier.padding(PostcardSpace.Panel), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                PostcardLabel(stringResource(R.string.today_before_flight_hotel))
                PostcardLabel("${f.airline} ${f.number}")
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                PassStop(f.fromAirport, f.fromCity, "%02d:%02d".format(f.departHour, f.departMinute), Alignment.Start)
                Text("✈", Modifier.weight(1f), style = PostcardType.Heading, color = KosStamp, textAlign = TextAlign.Center)
                PassStop(f.toAirport, f.toCity, "%02d:%02d".format(f.arriveHour, f.arriveMinute), Alignment.End)
            }
        }
        Canvas(Modifier.fillMaxWidth().height(14.dp)) {
            drawLine(
                dash, Offset(14.dp.toPx(), size.height / 2), Offset(size.width - 14.dp.toPx(), size.height / 2),
                strokeWidth = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx()))
            )
            drawCircle(notch, 7.dp.toPx(), Offset(0f, size.height / 2))
            drawCircle(notch, 7.dp.toPx(), Offset(size.width, size.height / 2))
        }
        Column(Modifier.padding(PostcardSpace.Panel), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            PostcardLabel("${f.weekday} %02d.%02d. · ${stringResource(R.string.today_before_hotel)}".format(f.day, f.month))
            Text("${TripData.HOTEL_NAME} · ${TripData.HOTEL_PLACE}", style = PostcardType.Body, color = KosInk)
        }
    }
}

@Composable
private fun PassStop(code: String, city: String, time: String, align: Alignment.Horizontal) {
    Column(horizontalAlignment = align) {
        Text(code, style = PostcardType.Numeral.copy(fontSize = 36.sp), color = KosInk)
        PostcardLabel(city)
        Text(time, style = PostcardType.Heading, color = KosInk)
    }
}

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
        Text(s.phrase, style = PostcardType.Quote, color = KosInk)
        if (s.days == 1L) {
            PaperPanel {
                PostcardLabel(stringResource(R.string.today_before_soon_label))
                Text(
                    stringResource(R.string.today_before_soon_body, TripData.CHECKED_BAG_KG, TripData.CARRY_ON_KG),
                    style = PostcardType.Body, color = KosInk
                )
            }
        }
        BoardingPass(flight)
        if (saved.isNotEmpty()) {
            PaperPanel(modifier = Modifier.clickable(onClick = onOpenTips)) {
                PostcardLabel("${stringResource(R.string.today_before_saved)} · ${saved.size}")
                saved.take(4).forEach { Text("• ${it.title}", style = PostcardType.Body, color = KosInk) }
            }
        }
    }
}
