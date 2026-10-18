package com.example.kosurlaub.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.ui.theme.HomeAccent
import com.example.kosurlaub.ui.theme.KosInk
import com.example.kosurlaub.ui.theme.KosNav
import com.example.kosurlaub.ui.theme.KosOnAccent
import com.example.kosurlaub.ui.theme.KosSans

/** Tagesleiste über alle Reisetage. [selected] ist der gewählte Tag (Index ab 1) oder null. */
@Composable
fun DayStrip(
    days: List<TripDay>,
    todayIndex: Int?,
    selected: Int?,
    onSelect: (TripDay) -> Unit
) {
    val state = rememberLazyListState()
    LaunchedEffect(Unit) {
        (selected ?: todayIndex)?.let { state.scrollToItem((it - 1).coerceAtLeast(0)) }
    }
    LazyRow(
        state = state,
        modifier = Modifier
            .fillMaxWidth()
            .background(KosNav)
            .testTag("day-strip"),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(days, key = { it.index }) { day ->
            val isSelected = day.index == selected
            val isToday = day.index == todayIndex
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(44.dp)
                    .background(if (isSelected) HomeAccent else KosInk.copy(alpha = 0.07f), RoundedCornerShape(10.dp))
                    .border(
                        if (isToday && !isSelected) BorderStroke(1.5.dp, HomeAccent) else BorderStroke(0.dp, Color.Transparent),
                        RoundedCornerShape(10.dp)
                    )
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(day) })
                    .padding(vertical = 6.dp)
                    .testTag("day-${day.index}")
            ) {
                val color = if (isSelected) KosOnAccent else KosInk
                Text(day.weekday, color = color.copy(alpha = 0.75f), fontSize = 10.sp, fontFamily = KosSans)
                Text(
                    day.day.toString(), color = color, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, fontFamily = KosSans
                )
            }
        }
    }
}
