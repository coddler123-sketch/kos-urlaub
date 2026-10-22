package com.example.kosurlaub.ui.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.kosurlaub.ui.theme.*

private const val PASSES = "passes"

/** Tab "Listen": Packliste (nach Taschen) und Aufgaben vor der Abreise, alles lokal gespeichert. */
@Composable
fun ListsScreen(padding: PaddingValues, scrollToTopRequest: Int, startOnPasses: Boolean = false) {
    val listState = rememberLazyListState()
    LaunchedEffect(scrollToTopRequest) {
        if (scrollToTopRequest > 0) listState.animateScrollToItem(0)
    }
    var key by rememberSaveable { mutableStateOf(if (startOnPasses) PASSES else ListStore.PACKING) }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().testTag("lists-screen").padding(padding),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Listen", style = PostcardType.Title, color = KosInk)
                PostcardLabel("Vor der Abreise")
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PostcardChip("Packen", key == ListStore.PACKING, { key = ListStore.PACKING })
                PostcardChip("Aufgaben", key == ListStore.TASKS, { key = ListStore.TASKS })
                PostcardChip("Bordkarten", key == PASSES, { key = PASSES })
            }
        }
        item(key = key) { if (key == PASSES) PassesPanel() else ListPanel(key) }
    }
}

@Composable
private fun ListPanel(key: String) {
    val context = LocalContext.current
    var items by remember { mutableStateOf(ListStore.load(context, key)) }
    var draft by remember { mutableStateOf("") }
    var bag by remember { mutableStateOf(ListStore.bags.first()) }

    fun update(new: List<ListEntry>) {
        items = new
        ListStore.save(context, key, new)
    }

    fun add() {
        val text = draft.trim()
        if (text.isNotEmpty()) {
            update(items + ListEntry(if (key == ListStore.PACKING) bag else "", text, "", false))
            draft = ""
        }
    }

    val (done, total) = ListStore.progress(items)
    PaperPanel(Modifier.testTag("lists-panel-$key")) {
        PostcardLabel("$done von $total erledigt")
        LinearProgressIndicator(
            progress = { if (total == 0) 0f else done / total.toFloat() },
            color = HomeAccent,
            trackColor = HomeCardBorder,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp)
        )
        val groups = if (key == ListStore.PACKING) ListStore.bags else listOf("")
        groups.forEach { group ->
            val rows = items.withIndex().filter { it.value.group == group }
            if (rows.isNotEmpty()) {
                if (group.isNotEmpty()) {
                    Text(group, style = PostcardType.Heading, color = KosInk, modifier = Modifier.padding(top = 12.dp))
                }
                rows.forEach { (index, item) ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = item.done,
                            onCheckedChange = { update(items.toMutableList().also { l -> l[index] = item.copy(done = it) }) },
                            colors = CheckboxDefaults.colors(checkedColor = HomeAccent, uncheckedColor = KosInk.copy(alpha = 0.5f))
                        )
                        Column(Modifier.weight(1f)) {
                            Text(item.text, style = PostcardType.Body, color = KosInk.copy(alpha = if (item.done) 0.5f else 0.95f))
                            if (item.hint.isNotEmpty()) {
                                Text(item.hint, style = PostcardType.Body, color = KosInk.copy(alpha = 0.6f))
                            }
                        }
                        TextButton(onClick = { update(items.filterIndexed { i, _ -> i != index }) }) {
                            Text("×", color = KosInk.copy(alpha = 0.6f), style = PostcardType.Heading)
                        }
                    }
                }
            }
        }
        if (key == ListStore.PACKING) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
                ListStore.bags.forEach { PostcardChip(it, bag == it, { bag = it }) }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            PostcardTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Neuer Punkt", style = PostcardType.Body) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { add() }),
                modifier = Modifier.weight(1f).testTag("lists-input")
            )
            TextButton(onClick = ::add, modifier = Modifier.testTag("lists-add")) {
                Text("Hinzufügen", color = HomeAccent)
            }
        }
    }
}
