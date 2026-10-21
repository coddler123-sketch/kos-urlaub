package com.example.kosurlaub.ui.home.today
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.home.isReturnPassVisible

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    onOpenTips: () -> Unit
) {
    val context = LocalContext.current
    val now = AppClock.nowMillis()
    val athens = TimeZone.getTimeZone("Europe/Athens")
    val kosNow = Calendar.getInstance(athens).apply { timeInMillis = now }
    val date = SimpleDateFormat("EEEE, d. MMMM", Locale.GERMAN).apply { timeZone = athens }.format(kosNow.time)
    val saved = compassTips.filter { it.id in CompassPreferences.saved(context) }
    val rawWeather: KosWeather? = WeatherRepository.cached(context)
    var sea by remember { mutableStateOf(MarineWeatherRepository.cached(context)?.seaSurfaceTemperatureCelsius) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            MarineWeatherRepository.fetch()?.also { MarineWeatherRepository.save(context.applicationContext, it) }
        }?.let { sea = it.seaSurfaceTemperatureCelsius }
    }
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
            if (isReturnPassVisible(now)) {
                val back = TripData.inbound
                BoardingPass(
                    back,
                    stringResource(R.string.today_return_flight),
                    "${back.weekday} %02d.%02d. · ${stringResource(R.string.today_return_checkout)}".format(back.day, back.month),
                    "${TripData.HOTEL_CHECK_OUT} · ${TripData.HOTEL_NAME}"
                )
            }
            PaperPanel(modifier = Modifier.clickable(onClick = onOpenTips)) {
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
                PaperPanel(modifier = Modifier.clickable(onClick = onOpenTips)) {
                    PostcardLabel(stringResource(R.string.today_tip))
                    Text(it.tip.title, style = PostcardType.Heading, color = KosInk)
                    Text(it.reason, style = PostcardType.Body, color = KosInk)
                }
            }
        }
    }
}

@Composable
private fun Fact(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = PostcardType.Numeral.copy(fontSize = PostcardType.Heading.fontSize), color = KosInk)
        PostcardLabel(label)
    }
}
