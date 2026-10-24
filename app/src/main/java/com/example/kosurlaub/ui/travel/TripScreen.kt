package com.example.kosurlaub.ui.travel

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.kosurlaub.R
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.home.today.BoardingPass
import com.example.kosurlaub.ui.home.AppClock
import com.example.kosurlaub.ui.home.today.CheckInCard
import com.example.kosurlaub.ui.home.today.CheckInWindow
import com.example.kosurlaub.ui.lists.PassesScreen
import com.example.kosurlaub.ui.theme.KosInk
import com.example.kosurlaub.ui.theme.PostcardLabel
import com.example.kosurlaub.ui.theme.PostcardType

/** Reise-Tab: Flüge und Bordkarten (Check-in erst eine Woche vorher), darunter Gepäck und Hotel. */
@Composable
fun TripScreen(padding: PaddingValues, scrollToTopRequest: Int) {
    val context = LocalContext.current
    val openMap: (String) -> Unit = { query ->
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(query)}"))) }
    }
    val out = TripData.outbound
    val back = TripData.inbound
    val hotel = stringResource(R.string.today_before_hotel)
    val checkOut = stringResource(R.string.today_return_checkout)
    PassesScreen(
        padding = padding,
        scrollToTopRequest = scrollToTopRequest,
        top = {
            item {
                Column {
                    Text("Reise", style = PostcardType.Title, color = KosInk, modifier = Modifier.testTag("trip-screen"))
                    PostcardLabel("Flug & Hotel")
                }
            }
            if (CheckInWindow.isCardVisible(AppClock.nowMillis(), out.departureMillis())) item { CheckInCard() }
            item {
                BoardingPass(
                    out, "Hinflug", "${out.weekday} %02d.%02d. · $hotel".format(out.day, out.month),
                    "${TripData.HOTEL_NAME} · ${TripData.HOTEL_PLACE}"
                )
            }
            item {
                BoardingPass(
                    back, stringResource(R.string.today_return_flight), "${back.weekday} %02d.%02d. · $checkOut".format(back.day, back.month),
                    "${TripData.HOTEL_CHECK_OUT} · ${TripData.HOTEL_NAME}"
                )
            }
        },
        bottom = {
            item { LuggageInfoCard() }
            item { HotelInfoCard(onOpenMap = openMap) }
        }
    )
}
