package com.example.kosurlaub.ui.home.today

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.home.AppClock
import com.example.kosurlaub.ui.theme.KosInk
import com.example.kosurlaub.ui.theme.PaperPanel
import com.example.kosurlaub.ui.theme.PostcardLabel
import com.example.kosurlaub.ui.theme.PostcardType
import java.util.concurrent.TimeUnit

/** Online-Check-in: öffnet [leadHours] Stunden vor Abflug. */
object CheckInWindow {
    sealed interface State {
        data class Waiting(val opensAtMillis: Long, val remainingMillis: Long) : State
        data object Open : State
        data object Gone : State
    }

    /** Bei Condor öffnet der Online-Check-in 24 Stunden vor Abflug. */
    const val LEAD_HOURS = 24

    /** Die Check-in-Karte erscheint erst so viele Tage vor Abflug. */
    const val SHOW_LEAD_DAYS = 7

    fun isCardVisible(nowMillis: Long, departureMillis: Long): Boolean =
        nowMillis in (departureMillis - TimeUnit.DAYS.toMillis(SHOW_LEAD_DAYS.toLong())) until departureMillis

    fun state(nowMillis: Long, departureMillis: Long, leadHours: Int): State {
        val opensAt = departureMillis - TimeUnit.HOURS.toMillis(leadHours.toLong())
        return when {
            nowMillis >= departureMillis -> State.Gone
            nowMillis >= opensAt -> State.Open
            else -> State.Waiting(opensAt, opensAt - nowMillis)
        }
    }

    /** "2 Tage 3 Std" bzw. "5 Std 10 Min". */
    fun remainingLabel(millis: Long): String {
        val days = TimeUnit.MILLISECONDS.toDays(millis)
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return if (days > 0) "$days ${if (days == 1L) "Tag" else "Tage"} $hours Std" else "$hours Std $minutes Min"
    }
}

@Composable
internal fun CheckInCard() {
    val state = CheckInWindow.state(AppClock.nowMillis(), TripData.outbound.departureMillis(), CheckInWindow.LEAD_HOURS)
    if (state is CheckInWindow.State.Gone) return
    PaperPanel(Modifier.testTag("checkin-card")) {
        PostcardLabel("Online-Check-in")
        Text(
            when (state) {
                is CheckInWindow.State.Waiting -> "Öffnet in ${CheckInWindow.remainingLabel(state.remainingMillis)}"
                else -> "Ist offen. Bordkarten jetzt holen."
            },
            style = PostcardType.Heading, color = KosInk
        )
        Text("Bei Condor ${CheckInWindow.LEAD_HOURS} Stunden vor Abflug", style = PostcardType.Body, color = KosInk.copy(alpha = 0.7f))
    }
}
