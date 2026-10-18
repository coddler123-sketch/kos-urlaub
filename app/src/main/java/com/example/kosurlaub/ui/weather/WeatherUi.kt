package com.example.kosurlaub.ui.weather
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ForecastEntry(val weekday: String, val iconRes: Int, val minTemp: Int, val maxTemp: Int)

data class WeatherSnapshot(
    val temperatureLabel: String,
    val apparentTemperatureLabel: String,
    val humidityLabel: String,
    val precipitationLabel: String,
    val windSpeedLabel: String,
    val iconRes: Int,
    val forecastDays: List<ForecastEntry>,
    val spokenReport: String
)

@Composable
fun WeatherCard(
    s: HomeState,
    isRefreshing: Boolean,
    reloadDone: Boolean,
    hasError: Boolean,
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeCardColor)
            .border(1.dp, HomeCardBorder, HomeCardShape)
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        if (s.weather != null) {
            if (reloadDone) {
                Text(
                    text = "✓",
                    color = Color(0xFF66DD88),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(1.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(48.dp)
                        .clickable(enabled = !isRefreshing, onClick = onRefresh)
                        .clearAndSetSemantics {
                            contentDescription = "Wetter aktualisieren"
                            if (isRefreshing) {
                                disabled()
                            } else {
                                onClick {
                                    onRefresh()
                                    true
                                }
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_refresh_subtle),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(17.dp)
                            .padding(1.dp),
                        alpha = if (isRefreshing) 0.25f else 0.55f
                    )
                }
            }
        }
        if (s.weather == null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TIGAKI-WETTER",
                        color = HomeAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = KosSans,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = when {
                            isRefreshing -> "Wetter wird geladen"
                            hasError -> "Wetter konnte nicht geladen werden"
                            else -> "Noch keine Wetterdaten"
                        },
                        color = KosInk,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = KosSans
                    )
                }
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .width(104.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(HomeAccent.copy(alpha = 0.16f))
                        .clickable(enabled = !isRefreshing, onClick = onRefresh)
                        .clearAndSetSemantics {
                            contentDescription = "Wetter jetzt laden"
                            if (isRefreshing) disabled() else onClick { onRefresh(); true }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            isRefreshing -> "Lädt …"
                            hasError -> "Erneut"
                            else -> "Jetzt laden"
                        },
                        color = HomeAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = KosSans
                    )
                }
            }
        } else {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 48.dp)
                ) {
                    Image(
                        painter = painterResource(s.weather.iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .height(26.dp)
                            .width(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tigaki jetzt",
                            color = KosInk.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = KosSans
                        )
                        Text(
                            text = s.weather.temperatureLabel,
                            color = KosInk,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = KosSans
                        )
                        Text(
                            text = s.weather.apparentTemperatureLabel,
                            color = KosInk.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontFamily = KosSans
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Wind ${s.weather.windSpeedLabel} · Luftfeuchte ${s.weather.humidityLabel} · Regen ${s.weather.precipitationLabel}",
                    color = KosInk.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontFamily = KosSans,
                    modifier = Modifier.padding(end = 48.dp)
                )
                if (s.weather.forecastDays.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(7.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        s.weather.forecastDays.forEach { day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.weekday,
                                    color = KosInk.copy(alpha = 0.75f),
                                    fontSize = 11.sp,
                                    fontFamily = KosSans
                                )
                                Image(
                                    painter = painterResource(day.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(vertical = 2.dp)
                                )
                                Text(
                                    text = "${day.maxTemp}°",
                                    color = KosInk,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = KosSans
                                )
                                Text(
                                    text = "${day.minTemp}°",
                                    color = KosInk.copy(alpha = 0.75f),
                                    fontSize = 11.sp,
                                    fontFamily = KosSans
                                )
                            }
                        }
                    }
                }
                if (s.sunriseLabel != null || s.sunsetLabel != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (s.sunriseLabel != null) {
                        Text(
                            text = "🌄  Sonnenaufgang: ${s.sunriseLabel}",
                            color = KosInk.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontFamily = KosSans
                        )
                    }
                    if (s.sunsetLabel != null) {
                        Text(
                            text = "🌅  Sonnenuntergang: ${s.sunsetLabel}",
                            color = KosInk.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontFamily = KosSans,
                            modifier = Modifier.padding(top = if (s.sunriseLabel != null) 2.dp else 0.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = s.weatherStatus,
                    color = KosInk.copy(alpha = 0.65f),
                    fontSize = 10.sp,
                    fontFamily = KosSans,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun BeachWindRadarCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeCardColor)
            .border(1.dp, HomeCardBorder, HomeCardShape)
            .padding(16.dp)
    ) {
        Text(
            "STRAND- & WIND-RADAR 🏖️💨",
            color = HomeAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Welcher Strand passt heute am besten?",
            color = KosInk,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSans
        )
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x25000000))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    " Nordküste (Tigaki & Umgebung)",
                    color = KosInk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Flach abfallendes Wasser • Gut zum Schwimmen (Tigaki, Marmari) • Windverhältnisse vor Ort prüfen",
                    color = KosInk.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x1A000000))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    " Westküste & Prasonisi",
                    color = KosInk.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Auffrischender Wind & Wellen • Ideal für Windsurfer & Kitesurfer",
                    color = KosInk.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
