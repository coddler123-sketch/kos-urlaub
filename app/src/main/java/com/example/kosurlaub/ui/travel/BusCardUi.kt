package com.example.kosurlaub.ui.travel

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.kosurlaub.ui.theme.HomeAccent
import com.example.kosurlaub.ui.theme.KosInk
import com.example.kosurlaub.ui.theme.PostcardLabel
import com.example.kosurlaub.ui.theme.PostcardType

@Composable
internal fun BusCard(onOpenSite: () -> Unit) {
    TravelCardContainer {
        PostcardLabel("Bus Tigaki ↔ Kos-Stadt", Modifier.testTag("bus-card"))
        Spacer(Modifier.height(8.dp))
        if (KtelKosTimetable.isVerified) {
            val first = KtelKosTimetable.routes.first()
            // Hinweis und Quelle gelten für alle Fahrpläne und stehen deshalb nur einmal hier.
            Text(first.validity.substringAfter(" · "), style = PostcardType.Body, color = KosInk.copy(alpha = 0.8f))
            Text(
                "Quelle: ${first.source} (Stand ${first.asOf})",
                style = PostcardType.Label, color = KosInk.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(12.dp))
            KtelKosTimetable.routes.forEach { route ->
                Text("${route.from} → ${route.to}", style = PostcardType.Heading, color = KosInk)
                PostcardLabel(route.validity.substringBefore(" · "))
                Spacer(Modifier.height(4.dp))
                Text(
                    route.departures.joinToString("  ·  ") { it.time },
                    style = PostcardType.Body,
                    color = KosInk
                )
                Spacer(Modifier.height(12.dp))
            }
        } else {
            Text(
                "Eigener Fahrplan noch nicht verifiziert. Aktuelle Zeiten und Preise bitte bei KTEL Kos nachsehen.",
                style = PostcardType.Body,
                color = KosInk,
                modifier = Modifier.testTag("bus-unverified")
            )
        }
        TextButton(onClick = onOpenSite, modifier = Modifier.testTag("bus-open-site")) {
            Text("KTEL KOS ÖFFNEN", style = PostcardType.Label, color = HomeAccent)
        }
    }
}
