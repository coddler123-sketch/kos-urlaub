package com.example.kosurlaub.ui.travel

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
import com.example.kosurlaub.ui.theme.HomeAccent
import com.example.kosurlaub.ui.theme.KosInk

@Composable
internal fun TripInfoCard(onOpenMap: (String) -> Unit) {
    TravelCardContainer {
        Text(
            "REISE & HOTEL ✈️",
            color = HomeAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.testTag("trip-info-card")
        )
        Spacer(Modifier.height(8.dp))
        InfoLine("Hinflug", TripData.outbound.summary())
        InfoLine("Rückflug", TripData.inbound.summary())
        InfoLine(
            "Gepäck",
            "${TripData.CHECKED_BAG_KG} kg aufgegeben, ${TripData.CARRY_ON_KG} kg Handgepäck " +
                "(max. ${TripData.CARRY_ON_SIZE}) pro Person"
        )
        InfoLine("Hotel", "${TripData.HOTEL_NAME}, ${TripData.HOTEL_PLACE}, ${TripData.HOTEL_POSTAL_CODE} Kos")
        InfoLine("Zimmer", "${TripData.ROOM}, ${TripData.BOARD}")
        InfoLine("Check-in / Check-out", "${TripData.HOTEL_CHECK_IN} / ${TripData.HOTEL_CHECK_OUT}")
        InfoLine("Veranstalter", TripData.OPERATOR)
        Spacer(Modifier.height(4.dp))
        TextButton(
            onClick = { onOpenMap("${TripData.HOTEL_LAT},${TripData.HOTEL_LON}(${TripData.HOTEL_NAME})") },
            modifier = Modifier.testTag("trip-info-map")
        ) {
            Text("📍 Hotel auf der Karte", color = HomeAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun InfoLine(label: String, text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label.uppercase(), color = KosInk.copy(alpha = 0.75f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text, color = KosInk, fontSize = 12.sp, lineHeight = 17.sp)
    }
}
