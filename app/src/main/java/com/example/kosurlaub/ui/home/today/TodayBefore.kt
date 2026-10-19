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
