package com.example.kosurlaub.ui.travel

import com.example.kosurlaub.ui.theme.PostcardLabel
import com.example.kosurlaub.ui.theme.PostcardType
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.compass.compassTips
import com.example.kosurlaub.ui.theme.HomeAccent
import com.example.kosurlaub.ui.theme.KosInk

@Composable
internal fun TripInfoCard(onOpenMap: (String) -> Unit) {
    TravelCardContainer {
        PostcardLabel("Reise & Hotel", Modifier.testTag("trip-info-card"))
        Spacer(Modifier.height(8.dp))
        InfoLine("Hinflug", TripData.outbound.summary())
        InfoLine("Rückflug", TripData.inbound.summary())
        InfoLine(
            "Gepäck",
            "${TripData.CHECKED_BAG_KG} kg aufgegeben, ${TripData.CARRY_ON_KG} kg Handgepäck " +
                "(max. ${TripData.CARRY_ON_SIZE}) pro Person"
        )
        InfoLine("Hotel", "${TripData.HOTEL_NAME}, ${TripData.HOTEL_PLACE}, ${TripData.HOTEL_POSTAL_CODE} Kos")
        InfoLine("Veranstalter", TripData.OPERATOR)
        Spacer(Modifier.height(4.dp))
        TextButton(
            onClick = { onOpenMap("${TripData.HOTEL_LAT},${TripData.HOTEL_LON}(${TripData.HOTEL_NAME})") },
            modifier = Modifier.testTag("trip-info-map")
        ) {
            Text("HOTEL AUF DER KARTE", style = PostcardType.Label, color = HomeAccent)
        }
    }
}

@Composable
internal fun HotelInfoCard() {
    TravelCardContainer {
        PostcardLabel("Hotel auf einen Blick", Modifier.testTag("hotel-info-card"))
        Spacer(Modifier.height(8.dp))
        hotelDetails.forEach { InfoLine(it.title, it.text) }
        InfoLine("Bewertungen", HOTEL_REVIEW_SUMMARY)
        Spacer(Modifier.height(4.dp))
        Text(HOTEL_SOURCE_NOTE, style = PostcardType.Label, color = KosInk.copy(alpha = 0.7f))
    }
}

@Composable
private fun InfoLine(label: String, text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.padding(vertical = 4.dp)) {
        PostcardLabel(label)
        Text(text, style = PostcardType.Body, color = KosInk)
    }
}

private val goodToKnow = listOf(
    "Geld" to "Auf Kos gilt der Euro. Karte geht in Supermärkten und den meisten Tavernen. Bargeld für Bus, Boote und kleine Kioske.",
    "Strom" to "Wie zu Hause: Typ C und F, 230 Volt. Kein Adapter nötig.",
    "Flughafen" to "KGS ist klein, der Ausgang ist zu Fuß. Tigaki liegt rund 15 Minuten vom Flughafen. Den Transfer vorher klären.",
    "Sonne und Wind" to "Mittags Schatten suchen, Wasser mitnehmen. Der Meltemi (Nordwind) ist im September schwächer als im Hochsommer, Südstrände sind bei Wind oft ruhiger.",
    "Rückreise" to "Der Rückflug geht um 11:35 Uhr. Transfer am Vorabend an der Rezeption bestätigen und früh frühstücken."
)

@Composable
internal fun GoodToKnowCard() {
    TravelCardContainer {
        PostcardLabel("Gut zu wissen", Modifier.testTag("good-to-know-card"))
        Spacer(Modifier.height(8.dp))
        goodToKnow.forEach { (label, text) -> InfoLine(label, text) }
    }
}

/** Die Mobilitäts-Tipps des Guides, hier gebündelt statt als eigene Kategorie. */
@Composable
internal fun MobilityTipsCard() {
    val tips = compassTips.filter { it.category == "Mobilität" }
    if (tips.isEmpty()) return
    TravelCardContainer {
        PostcardLabel("Tipps zu Bus und Rückfahrt", Modifier.testTag("mobility-tips-card"))
        Spacer(Modifier.height(8.dp))
        tips.forEach { InfoLine(it.title, "${it.description} ${it.note}.") }
    }
}
