package com.example.kosurlaub.ui.home.today

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.theme.*
/** Flug als Bordkarte: Codes und Zeiten oben, Abreißkante mit Kerben, darunter eine frei wählbare Fußzeile (Hotel bzw. Check-out). */
@Composable
internal fun BoardingPass(f: TripData.Flight, title: String, footerLabel: String, footerText: String) {
    val notch = KosBg
    val dash = HomeCardBorder
    Column(
        Modifier.fillMaxWidth().clip(PostcardShape.Panel).background(HomeCardColor)
            .border(1.dp, HomeCardBorder, PostcardShape.Panel)
    ) {
        Column(Modifier.padding(PostcardSpace.Panel), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                PostcardLabel(title)
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
            PostcardLabel(footerLabel)
            Text(footerText, style = PostcardType.Body, color = KosInk)
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

