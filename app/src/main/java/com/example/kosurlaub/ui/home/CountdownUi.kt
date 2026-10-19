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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun CountdownProgress(fraction: Float) {
    Column(modifier = Modifier.padding(top = 10.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "VORFREUDE BIS ZUM ABFLUG",
                color = KosInk.copy(alpha = 0.75f),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = KosSans,
                letterSpacing = 0.6.sp
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${(fraction * 100).roundToInt()} %",
                color = KosInk.copy(alpha = 0.75f),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = KosSans
            )
        }
        Spacer(Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(2.dp))
                    .background(KosInk.copy(alpha = 0.2f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(HomeAccent)
            )
        }
    }
}

