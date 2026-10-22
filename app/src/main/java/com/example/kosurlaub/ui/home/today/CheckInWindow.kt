package com.example.kosurlaub.ui.home.today

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.kosurlaub.TripData
import com.example.kosurlaub.ui.home.AppClock
import com.example.kosurlaub.ui.theme.HomeAccent
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

    const val DEFAULT_LEAD_HOURS = 24
    private const val PREFS = "kos_checkin"
    private const val KEY_LEAD = "lead_hours"

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
        return if (days > 0) "$days Tage $hours Std" else "$hours Std $minutes Min"
    }

    fun leadHours(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_LEAD, DEFAULT_LEAD_HOURS)

    fun saveLeadHours(context: Context, hours: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_LEAD, hours).apply()
    }
}

@Composable
internal fun CheckInCard() {
    val context = LocalContext.current
    var lead by remember { mutableIntStateOf(CheckInWindow.leadHours(context)) }
    val state = CheckInWindow.state(AppClock.nowMillis(), TripData.outbound.departureMillis(), lead)
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
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$lead Std vor Abflug (bei Condor prüfen)", style = PostcardType.Body, color = KosInk.copy(alpha = 0.7f))
            Row {
                TextButton({ lead = (lead - 1).coerceAtLeast(1); CheckInWindow.saveLeadHours(context, lead) }) {
                    Text("−", color = HomeAccent)
                }
                TextButton({ lead = (lead + 1).coerceAtMost(72); CheckInWindow.saveLeadHours(context, lead) }) {
                    Text("+", color = HomeAccent)
                }
            }
        }
    }
}
