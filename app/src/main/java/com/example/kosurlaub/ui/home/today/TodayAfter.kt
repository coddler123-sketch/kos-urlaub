package com.example.kosurlaub.ui.home.today

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.kosurlaub.R
import com.example.kosurlaub.ui.compass.CompassPreferences
import com.example.kosurlaub.ui.home.HomeState
import com.example.kosurlaub.ui.home.PolaroidPhoto
import com.example.kosurlaub.ui.home.TripDays
import com.example.kosurlaub.ui.theme.*

/** Heute nach dem Urlaub: Rückblick mit Foto und Zahlen. */
@Composable
internal fun TodayAfter(s: HomeState, heroPhoto: ImageBitmap?) {
    val context = LocalContext.current
    val visited = CompassPreferences.visited(context).size
    Column(verticalArrangement = Arrangement.spacedBy(PostcardSpace.Gap), horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.today_after_title), style = PostcardType.Title, color = KosInk)
        PolaroidPhoto(imageRes = s.backgroundRes, caption = "Kos", photo = heroPhoto)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Stat(TripDays.all.size.toString(), stringResource(R.string.today_after_days))
            Stat(visited.toString(), stringResource(R.string.today_after_visited))
        }
    }
}

@Composable
private fun Stat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = PostcardType.Numeral, color = KosNumeral)
        PostcardLabel(label)
    }
}
