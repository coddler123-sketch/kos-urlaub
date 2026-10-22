package com.example.kosurlaub.ui.lists

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.delay
import com.example.kosurlaub.ui.home.AppClock
import com.example.kosurlaub.ui.home.today.CheckInWindow
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.theme.KosSans
import com.example.kosurlaub.ui.theme.KosSerif

private val Backdrop = Color(0xFF14110F)
private val Paper = Color(0xFFFFFDF8)
private val Ink = Color(0xFF2B3A30)
private val Muted = Color(0xFF6B665C)
private val Stamp = Color(0xFFB5442E)

/**
 * Bordkarte allein auf dem Bildschirm: kein App-Rahmen, volle Helligkeit, Systemleisten weg.
 * Mit mehreren Karten (z. B. beide Reisenden) wischt man zwischen ihnen.
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun PassView(passes: List<Pass>, start: Int, onClose: () -> Unit) {
    val context = LocalContext.current
    val pager = rememberPagerState(initialPage = start.coerceIn(0, passes.lastIndex)) { passes.size }

    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        val before = window?.attributes?.screenBrightness
        window?.attributes = window?.attributes?.apply { screenBrightness = 1f }
        onDispose { window?.attributes = window?.attributes?.apply { screenBrightness = before ?: -1f } }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        val view = LocalView.current
        (view.parent as? DialogWindowProvider)?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).hide(WindowInsetsCompat.Type.systemBars())
        }
        Box(Modifier.fillMaxSize().background(Backdrop).testTag("pass-view")) {
            HorizontalPager(pager, Modifier.fillMaxSize()) { page ->
                PassPage(passes[page], page, passes.size, onClose)
            }
            Box(
                Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 20.dp)
                    .heightIn(min = 52.dp).clip(RoundedCornerShape(50)).background(Paper)
                    .clickable(onClick = onClose).padding(horizontal = 44.dp).testTag("pass-close"),
                contentAlignment = Alignment.Center
            ) { Text("Schließen", color = Ink, fontFamily = KosSans, fontWeight = FontWeight.SemiBold, fontSize = 17.sp) }
        }
    }
}

@Composable
private fun PassPage(pass: Pass, index: Int, total: Int, onClose: () -> Unit) {
    val context = LocalContext.current
    val barcode = remember(pass.id) { pass.payload?.let { BarcodeCodec.encode(it, pass.format.orEmpty(), 1000) } }
    val data = remember(pass.id) { pass.payload?.let { Bcbp.parse(it) } }
    var showFile by remember(pass.id) { mutableStateOf(barcode == null) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 56.dp, bottom = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                if (total > 1) "Karte ${index + 1} von $total · wischen" else "",
                color = Paper.copy(alpha = 0.7f), fontFamily = KosSans, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp)
            )
        }
        if (showFile || barcode == null) {
            val big = remember(pass.id) { PassStore.render(context, pass, 1400) }
            if (big != null) {
                Image(
                    big.asImageBitmap(), pass.label,
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White),
                    contentScale = ContentScale.FillWidth
                )
            }
        } else {
            Ticket(pass.label, data, barcode.asImageBitmap())
        }
        if (barcode != null) {
            TextButton({ showFile = !showFile }) {
                Text(if (showFile) "Zurück zur Bordkarte" else "Original ansehen", color = Paper.copy(alpha = 0.8f), fontFamily = KosSans)
            }
        }
        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun Ticket(label: String, data: BoardingData?, barcode: androidx.compose.ui.graphics.ImageBitmap) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Paper)) {
        if (data != null) {
            val flight = listOf(TripData.outbound, TripData.inbound).firstOrNull {
                it.fromAirport == data.from && it.toAirport == data.to
            }
            flight?.let { AirlineBand(it) }
            Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Field("BORDKARTE", label.substringBefore(" ·"))
                    Field("FLUG", data.flight, end = true)
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(data.from, fontFamily = KosSerif, fontWeight = FontWeight.Bold, fontSize = 46.sp, color = Ink)
                        flight?.let { Text(it.fromCity.uppercase(), fontFamily = KosSans, fontSize = 12.sp, color = Muted, letterSpacing = 1.sp) }
                    }
                    FlightPath(Modifier.weight(1f).padding(horizontal = 10.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(data.to, fontFamily = KosSerif, fontWeight = FontWeight.Bold, fontSize = 46.sp, color = Ink)
                        flight?.let { Text(it.toCity.uppercase(), fontFamily = KosSans, fontSize = 12.sp, color = Muted, letterSpacing = 1.sp) }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Field("PASSAGIER", data.passenger)
                    Field("SITZ", data.seat.ifEmpty { "–" }, end = true)
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Field(
                        "DATUM",
                        Bcbp.dayMonth(data.dayOfYear, TripData.outbound.year) + flight?.let { "  ·  %02d:%02d".format(it.departHour, it.departMinute) }.orEmpty()
                    )
                    Field("BUCHUNG", data.bookingRef, end = true)
                }
            }
        } else {
            Text(label, fontFamily = KosSerif, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Ink, modifier = Modifier.padding(20.dp))
        }
        Tear()
        Image(
            barcode, label,
            Modifier.fillMaxWidth().padding(20.dp).background(Color.White),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
private fun Field(label: String, value: String, end: Boolean = false) {
    Column(horizontalAlignment = if (end) Alignment.End else Alignment.Start) {
        Text(label, fontFamily = KosSans, fontSize = 11.sp, letterSpacing = 1.2.sp, color = Muted, textAlign = if (end) TextAlign.End else TextAlign.Start)
        Text(value, fontFamily = KosSans, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = Ink)
    }
}

/** Abreißkante: gestrichelte Linie mit Kerben links und rechts. */
@Composable
private fun Tear() {
    Box(Modifier.fillMaxWidth().height(28.dp).padding(top = 14.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val y = 0f
            drawLine(
                Color(0xFFCFC8BA), Offset(24.dp.toPx(), y), Offset(size.width - 24.dp.toPx(), y),
                strokeWidth = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 8.dp.toPx()))
            )
            drawCircle(Backdrop, radius = 12.dp.toPx(), center = Offset(0f, y))
            drawCircle(Backdrop, radius = 12.dp.toPx(), center = Offset(size.width, y))
        }
    }
}

private val CondorYellow = Color(0xFFFFCE00)
private val BandInk = Color(0xFF1B1B1B)

/** Kopfband in der Farbe der Fluggesellschaft mit Restzeit bis zum Abflug. */
@Composable
private fun AirlineBand(flight: TripData.Flight) {
    var now by remember { mutableStateOf(AppClock.nowMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            now = AppClock.nowMillis()
        }
    }
    val left = flight.departureMillis() - now
    Row(
        Modifier.fillMaxWidth().background(CondorYellow).padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(flight.airline.uppercase(), fontFamily = KosSans, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 2.sp, color = BandInk)
        Text(
            if (left > 0) "Abflug in ${CheckInWindow.remainingLabel(left)}" else "Abgeflogen",
            fontFamily = KosSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = BandInk
        )
    }
}

/** Gestrichelte Flugbahn zwischen den Flughäfen mit Flugzeug in der Mitte. */
@Composable
private fun FlightPath(modifier: Modifier = Modifier) {
    Box(modifier.height(40.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val y = size.height / 2
            drawLine(
                Color(0xFFCFC8BA), Offset(8.dp.toPx(), y), Offset(size.width - 8.dp.toPx(), y),
                strokeWidth = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 6.dp.toPx()))
            )
            drawCircle(Ink, 4.dp.toPx(), Offset(4.dp.toPx(), y))
            drawCircle(Ink, 4.dp.toPx(), Offset(size.width - 4.dp.toPx(), y))
        }
        Text("✈", fontSize = 24.sp, color = Stamp, modifier = Modifier.background(Paper).padding(horizontal = 6.dp))
    }
}
