package com.example.kosurlaub.ui.home
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

@Composable
fun CompassCard(onClick: () -> Unit) {
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeCardColor)
            .border(1.dp, HomeAccent.copy(alpha = 0.5f), HomeCardShape)
            .testTag("compass-link")
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        PostcardLabel("KOS KOMPASS")
        Spacer(Modifier.height(5.dp))
        Text("${compassTips.size} ausgewählte Kos-Tipps", color = KosInk, fontSize = PostcardMetrics.BODY_SP.sp, fontWeight = FontWeight.SemiBold, fontFamily = KosSans)
        Spacer(Modifier.height(4.dp))
        Text("Strände, Essen, Unterkünfte und Mobilität entdecken  ›", color = KosInk.copy(alpha = 0.8f), fontSize = PostcardMetrics.LABEL_SP.sp, lineHeight = 16.sp, fontFamily = KosSans)
    }
}

@Composable
fun TravelCard(onClick: () -> Unit) {
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeCardColor)
            .border(1.dp, HomeAccent.copy(alpha = 0.5f), HomeCardShape)
            .testTag("travel-link")
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        PostcardLabel(stringResource(R.string.travel_card_label))
        Spacer(Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.travel_card_title),
            color = KosInk,
            fontSize = PostcardMetrics.BODY_SP.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = KosSans
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.travel_card_description),
            color = KosInk.copy(alpha = 0.8f),
            fontSize = PostcardMetrics.LABEL_SP.sp,
            lineHeight = 16.sp,
            fontFamily = KosSans
        )
    }
}

@Composable
fun HomeQuickActions(
    onOpenTigaki: () -> Unit,
    onOpenPhrasebook: () -> Unit = {},
    onOpenEmergency: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        PostcardLabel("SCHNELLZUGRIFF")
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HomeQuickGridCard(
                title = "Bus Tigaki",
                subtitle = "Fahrplan & Abfahrt",
                testTag = "tigaki-bus-link",
                onClick = onOpenTigaki,
                modifier = Modifier.weight(1f),
                emphasized = true
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HomeQuickGridCard(
                title = "Sprachführer",
                subtitle = "Redewendungen",
                testTag = "phrasebook-link",
                onClick = onOpenPhrasebook,
                modifier = Modifier.weight(1f)
            )
            HomeQuickGridCard(
                title = "SOS Notfall",
                subtitle = "Arzt, Taxi & Hotel",
                testTag = "emergency-link",
                onClick = onOpenEmergency,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HomeQuickGridCard(
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false
) {
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = modifier
            .heightIn(min = 72.dp)
            .clip(HomeCardShape)
            .background(if (emphasized) HomeAccent.copy(alpha = 0.20f) else HomeCardColor)
            .border(1.dp, HomeAccent.copy(alpha = if (emphasized) 0.85f else 0.4f), HomeCardShape)
            .testTag(testTag)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = KosInk,
            fontSize = PostcardMetrics.BODY_SP.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSans
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitle,
            color = KosInk.copy(alpha = 0.8f),
            fontSize = PostcardMetrics.LABEL_SP.sp,
            fontFamily = KosSans
        )
    }
}

@Composable
fun FactCard(fact: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeCardColor)
            .border(1.dp, HomeCardBorder, HomeCardShape)
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        PostcardLabel("KOS-FAKT")
        Spacer(Modifier.height(5.dp))
        Text(
            text = fact,
            color = KosInk.copy(alpha = 0.95f),
            fontSize = PostcardMetrics.BODY_SP.sp,
            lineHeight = 18.sp,
            fontFamily = KosSans
        )
    }
}

@Composable
fun HighlightCard(highlight: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeCardColor)
            .border(1.dp, HomeCardBorder, HomeCardShape)
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        PostcardLabel("TAGESHÖHEPUNKT")
        Spacer(Modifier.height(5.dp))
        Text(
            text = highlight,
            color = KosInk.copy(alpha = 0.95f),
            fontSize = PostcardMetrics.BODY_SP.sp,
            lineHeight = 18.sp,
            fontFamily = KosSans
        )
    }
}

@Composable
fun CommunityCard(onClick: () -> Unit) {
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(HomeCardColor)
            .border(1.dp, HomeCardBorder, HomeCardShape)
            .testTag("community-link")
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        PostcardLabel(stringResource(R.string.community_label))
        Spacer(Modifier.height(5.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.community_title),
                    color = KosInk,
                    fontSize = PostcardMetrics.BODY_SP.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = KosSans
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.community_description),
                    color = KosInk.copy(alpha = 0.8f),
                    fontSize = PostcardMetrics.LABEL_SP.sp,
                    lineHeight = 16.sp,
                    fontFamily = KosSans
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.community_action),
                    color = HomeAccent,
                    fontSize = PostcardMetrics.LABEL_SP.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = KosSans
                )
            }
            Text(text = "↗", color = HomeAccent, fontSize = 20.sp)
        }
    }
}

@Composable
fun FlightDayTimelineCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(HomeCardShape)
            .background(KosPanelHigh)
            .border(1.dp, HomeAccent.copy(alpha = 0.8f), HomeCardShape)
            .padding(16.dp)
            .testTag("flight-day-timeline")
    ) {
        PostcardLabel("HEUTE IST ABFLUGTAG!")
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Reise-Timeline & Etappen",
            color = KosInk,
            fontSize = PostcardMetrics.BODY_SP.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSans
        )
        Spacer(Modifier.height(10.dp))
        TimelineItem("~12:45 Uhr", "Boarding & Abflugbereich", "Gepäck aufgegeben & Sicherheitskontrolle")
        TimelineItem("13:25 Uhr", "Abflug ab Hamburg (HAM) nach Kos", "Condor DE1640, Direktflug 13.09.2027")
        TimelineItem("17:45 Uhr", "Ankunft Flughafen Kos (KGS)", "Ortszeit Kos (+1 Std.)")
        TimelineItem("~19:00 Uhr", "Check-in ${TripData.HOTEL_NAME}", TripData.ROOM)
    }
}

@Composable
private fun TimelineItem(time: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = time,
            color = HomeAccent,
            fontSize = PostcardMetrics.BODY_SP.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = KosSans,
            modifier = Modifier.width(75.dp)
        )
        Column {
            Text(
                text = title,
                color = KosInk,
                fontSize = PostcardMetrics.BODY_SP.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = KosSans
            )
            Text(
                text = subtitle,
                color = KosInk.copy(alpha = 0.75f),
                fontSize = PostcardMetrics.LABEL_SP.sp,
                fontFamily = KosSans
            )
        }
    }
}
