package com.example.kosurlaub.ui.theme

import android.graphics.Typeface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Schmalschrift für Zahlen und Etiketten; wie in den Widgets die System-Schmalschrift. */
val KosCondensed = FontFamily(Typeface.create("sans-serif-condensed", Typeface.BOLD))

/** Textstile des Postkarten-Looks; Größen stehen in [PostcardMetrics]. Farbe setzt der Aufrufer. */
object PostcardType {
    val Title = TextStyle(
        fontFamily = KosSerif, fontWeight = FontWeight.Bold,
        fontSize = PostcardMetrics.TITLE_SP.sp, lineHeight = 32.sp
    )
    val Heading = TextStyle(
        fontFamily = KosSerif, fontWeight = FontWeight.Bold,
        fontSize = PostcardMetrics.HEADING_SP.sp, lineHeight = 24.sp
    )
    val Quote = TextStyle(
        fontFamily = KosSerif, fontStyle = FontStyle.Italic,
        fontSize = PostcardMetrics.QUOTE_SP.sp, lineHeight = 20.sp
    )
    val Body = TextStyle(
        fontFamily = KosSans, fontWeight = FontWeight.Normal,
        fontSize = PostcardMetrics.BODY_SP.sp, lineHeight = 20.sp
    )
    val Label = TextStyle(
        fontFamily = KosCondensed, fontWeight = FontWeight.Bold,
        fontSize = PostcardMetrics.LABEL_SP.sp, letterSpacing = 1.6.sp
    )
    val Numeral = TextStyle(
        fontFamily = KosCondensed, fontWeight = FontWeight.Bold,
        fontSize = PostcardMetrics.NUMERAL_SP.sp
    )
}

object PostcardSpace {
    val Screen = 16.dp
    val Gap = 10.dp
    val Panel = PostcardMetrics.PANEL_PADDING_DP.dp
}

object PostcardShape {
    val Panel = RoundedCornerShape(16.dp)
    val Photo = RoundedCornerShape(14.dp)
    val Chip = RoundedCornerShape(4.dp)
}
