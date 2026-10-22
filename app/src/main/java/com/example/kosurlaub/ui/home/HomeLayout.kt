package com.example.kosurlaub.ui.home

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.kosurlaub.ui.theme.HomeAccent
import com.example.kosurlaub.ui.theme.KosInk
import com.example.kosurlaub.ui.theme.PostcardType

/** Eine Karte der Startseite, die sich ausblenden und verschieben lässt. */
class HomeCard(val id: String, val label: String, val content: @Composable () -> Unit)

/** Reihenfolge und Sichtbarkeit der Startseiten-Karten je Phase, lokal gespeichert. */
object HomeLayout {
    private const val PREFS = "kos_home_layout"

    /** Gespeicherte Reihenfolge, unbekannte Karten entfallen, neue kommen ans Ende. */
    fun order(available: List<String>, saved: List<String>): List<String> =
        saved.filter { it in available }.distinct() + available.filter { it !in saved }

    fun move(order: List<String>, id: String, delta: Int): List<String> {
        val from = order.indexOf(id)
        val to = from + delta
        if (from < 0 || to !in order.indices) return order
        return order.toMutableList().apply { add(to, removeAt(from)) }
    }

    private fun list(context: Context, key: String): List<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(key, "").orEmpty()
            .split(",").filter { it.isNotBlank() }

    fun savedOrder(context: Context, phase: String) = list(context, "order_$phase")
    fun savedHidden(context: Context, phase: String) = list(context, "hidden_$phase").toSet()

    fun save(context: Context, phase: String, order: List<String>, hidden: Set<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("order_$phase", order.joinToString(","))
            .putString("hidden_$phase", hidden.joinToString(","))
            .apply()
    }
}

/** Zeigt [cards] in der gespeicherten Reihenfolge ohne die ausgeblendeten, dazu den Knopf "Anpassen". */
@Composable
fun CustomizableCards(phase: String, cards: List<HomeCard>) {
    val context = LocalContext.current
    val ids = cards.map { it.id }
    var order by remember { mutableStateOf(HomeLayout.order(ids, HomeLayout.savedOrder(context, phase))) }
    var hidden by remember { mutableStateOf(HomeLayout.savedHidden(context, phase)) }
    var editing by remember { mutableStateOf(false) }

    order.filter { it !in hidden }.forEach { id -> cards.first { it.id == id }.content() }
    TextButton({ editing = true }, Modifier.testTag("home-customize")) {
        Text("STARTSEITE ANPASSEN", style = PostcardType.Label, color = HomeAccent)
    }

    if (editing) {
        AlertDialog(
            onDismissRequest = { editing = false },
            confirmButton = { TextButton({ editing = false }) { Text("Fertig", color = HomeAccent) } },
            title = { Text("Startseite anpassen") },
            text = {
                androidx.compose.foundation.layout.Column {
                    order.forEach { id ->
                        val card = cards.first { it.id == id }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = id !in hidden,
                                onCheckedChange = {
                                    hidden = if (it) hidden - id else hidden + id
                                    HomeLayout.save(context, phase, order, hidden)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = HomeAccent)
                            )
                            Text(card.label, style = PostcardType.Body, color = KosInk, modifier = Modifier.weight(1f))
                            Row(horizontalArrangement = Arrangement.End) {
                                TextButton({ order = HomeLayout.move(order, id, -1); HomeLayout.save(context, phase, order, hidden) }) { Text("↑") }
                                TextButton({ order = HomeLayout.move(order, id, 1); HomeLayout.save(context, phase, order, hidden) }) { Text("↓") }
                            }
                        }
                    }
                }
            }
        )
    }
}
