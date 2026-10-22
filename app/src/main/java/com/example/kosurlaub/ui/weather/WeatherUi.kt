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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.platform.LocalContext
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
            .clip(PostcardShape.Panel)
            .background(HomeCardColor)
            .border(1.dp, HomeCardBorder, PostcardShape.Panel)
            .padding(PostcardSpace.Panel)
    ) {
        if (s.weather != null) {
            if (reloadDone) {
                Text(
                    text = "✓",
                    color = KosOk,
                    style = PostcardType.Heading,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
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
                        modifier = Modifier.align(Alignment.Center).size(20.dp),
                        colorFilter = ColorFilter.tint(KosInk),
                        alpha = if (isRefreshing) 0.25f else 0.7f
                    )
                }
            }
        }
        if (s.weather == null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    PostcardLabel("Kos · Tigaki")
                    Text(
                        text = when {
                            isRefreshing -> "Wetter wird geladen"
                            hasError -> "Wetter konnte nicht geladen werden"
                            else -> "Noch keine Wetterdaten"
                        },
                        style = PostcardType.Body,
                        color = KosInk
                    )
                }
                PostcardChip(
                    label = when {
                        isRefreshing -> "Lädt …"
                        hasError -> "Erneut"
                        else -> "Jetzt laden"
                    },
                    selected = false,
                    onClick = { if (!isRefreshing) onRefresh() }
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 48.dp)
                ) {
                    Image(
                        painter = painterResource(s.weather.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        colorFilter = ColorFilter.tint(KosInk)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        PostcardLabel("Kos · Tigaki jetzt")
                        Text(s.weather.temperatureLabel, style = PostcardType.Title, color = KosInk)
                        Text(s.weather.apparentTemperatureLabel, style = PostcardType.Body, color = KosInk.copy(alpha = 0.85f))
                    }
                }
                val sea = rememberSeaTemperature()
                Row(Modifier.fillMaxWidth()) {
                    WeatherFact("Meer", sea?.let { "${Math.round(it)}°" } ?: "–", Modifier.weight(1f))
                    WeatherFact("Wind", s.weather.windSpeedLabel, Modifier.weight(1f))
                    WeatherFact("Feuchte", s.weather.humidityLabel, Modifier.weight(1f))
                    WeatherFact("Regen", s.weather.precipitationLabel, Modifier.weight(1f))
                }
                if (s.weather.forecastDays.isNotEmpty()) {
                    WeatherDivider()
                    Row(modifier = Modifier.fillMaxWidth()) {
                        s.weather.forecastDays.forEach { day ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(day.weekday, style = PostcardType.Label, color = KosInk.copy(alpha = 0.75f))
                                Image(
                                    painter = painterResource(day.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp).padding(vertical = 2.dp),
                                    colorFilter = ColorFilter.tint(KosInk)
                                )
                                Text("${day.maxTemp}°", style = PostcardType.Label, color = KosInk)
                                Text("${day.minTemp}°", style = PostcardType.Label, color = KosInk.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
                WeatherDivider()
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    s.sunriseLabel?.let { PostcardLabel("Sonnenaufgang $it") }
                    s.sunsetLabel?.let { PostcardLabel("Sonnenuntergang $it") }
                    Text(
                        text = s.weatherStatus,
                        style = PostcardType.Label,
                        color = KosInk.copy(alpha = 0.65f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherFact(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = PostcardType.Numeral.copy(fontSize = PostcardType.Heading.fontSize), color = KosInk)
        PostcardLabel(label)
    }
}

@Composable
private fun WeatherDivider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(HomeCardBorder))
}

@Composable
fun BeachWindRadarCard(modifier: Modifier = Modifier) {
    PaperPanel(modifier = modifier) {
        PostcardLabel("Strand- & Wind-Radar")
        Spacer(Modifier.height(4.dp))
        Text("Welcher Strand passt heute am besten?", style = PostcardType.Heading, color = KosInk)
        WeatherRepository.cached(LocalContext.current)?.let { w ->
            Spacer(Modifier.height(8.dp))
            val from = w.windDirectionDegrees?.let { "aus ${BeachAdvice.directionName(it)}, " }.orEmpty()
            Text("Jetzt: Wind $from${w.windSpeedKmh} km/h", style = PostcardType.Body.copy(fontWeight = FontWeight.Bold), color = KosInk)
            Text(BeachAdvice.advice(w.windSpeedKmh, w.windDirectionDegrees), style = PostcardType.Body, color = KosInk)
        }
        Spacer(Modifier.height(8.dp))
        BeachOption(
            "Nordküste (Tigaki & Umgebung)",
            "Flach abfallendes Wasser • Gut zum Schwimmen (Tigaki, Marmari) • Windverhältnisse vor Ort prüfen"
        )
        Spacer(Modifier.height(8.dp))
        BeachOption(
            "Westküste & Prasonisi",
            "Auffrischender Wind & Wellen • Ideal für Windsurfer & Kitesurfer"
        )
    }
}

@Composable
private fun BeachOption(title: String, detail: String) {
    Column(Modifier.fillMaxWidth().clip(PostcardShape.Photo).background(KosPanelHigh).padding(PostcardSpace.Panel)) {
        Text(title, style = PostcardType.Body.copy(fontWeight = FontWeight.Bold), color = KosInk)
        Text(detail, style = PostcardType.Body.copy(fontSize = PostcardMetrics.BODY_SP.sp), color = KosInk.copy(alpha = 0.85f))
    }
}
