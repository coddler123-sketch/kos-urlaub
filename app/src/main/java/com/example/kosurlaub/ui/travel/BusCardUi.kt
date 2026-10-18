package com.example.kosurlaub.ui.travel

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.ui.theme.HomeAccent
import com.example.kosurlaub.ui.theme.KosInk

@Composable
internal fun BusCard(onOpenSite: () -> Unit) {
    TravelCardContainer {
        Text(
            "BUS TIGAKI ↔ KOS-STADT 🚌",
            color = HomeAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.testTag("bus-card")
        )
        Spacer(Modifier.height(8.dp))
        if (KtelKosTimetable.isVerified) {
            KtelKosTimetable.routes.forEach { route ->
                Text(
                    "${route.from} → ${route.to}",
                    color = KosInk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(route.validity, color = KosInk.copy(alpha = 0.75f), fontSize = 10.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    route.departures.joinToString("  ·  ") { it.time },
                    color = KosInk,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
                Text(
                    "Quelle: ${route.source} (Stand ${route.asOf})",
                    color = KosInk.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
                Spacer(Modifier.height(10.dp))
            }
        } else {
            Text(
                "Eigener Fahrplan noch nicht verifiziert. Aktuelle Zeiten und Preise bitte bei KTEL Kos nachsehen.",
                color = KosInk.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                modifier = Modifier.testTag("bus-unverified")
            )
        }
        TextButton(onClick = onOpenSite, modifier = Modifier.testTag("bus-open-site")) {
            Text("KTEL Kos öffnen", color = HomeAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
