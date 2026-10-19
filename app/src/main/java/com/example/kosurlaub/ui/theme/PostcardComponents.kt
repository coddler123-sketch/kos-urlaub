package com.example.kosurlaub.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Kleines Großbuchstaben-Etikett über Karten (z. B. "FLUG & HOTEL"). */
@Composable
fun PostcardLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = PostcardType.Label,
        color = KosInk.copy(alpha = 0.8f),
        modifier = modifier
    )
}

/** Rechteckiger Stempel-Chip, z. B. "TAGE". */
@Composable
fun StampChip(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = PostcardType.Label,
        color = KosInk,
        modifier = modifier
            .clip(PostcardShape.Chip)
            .background(KosPanelHigh)
            .padding(
                horizontal = PostcardMetrics.CHIP_H_PADDING_DP.dp,
                vertical = PostcardMetrics.CHIP_V_PADDING_DP.dp
            )
    )
}

/** Runder Stempel mit gestricheltem Rand, z. B. "TAG 4/16" oder Wetter. */
@Composable
fun StampBadge(top: String, bottom: String, modifier: Modifier = Modifier) {
    val color = KosStamp
    Column(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(KosBg.copy(alpha = 0.95f))
            .drawBehind {
                drawCircle(
                    color = color,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 2.dp.toPx()))
                    )
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(top, style = PostcardType.Label, color = color, textAlign = TextAlign.Center)
        Text(bottom, style = PostcardType.Label.copy(fontSize = PostcardType.Heading.fontSize), color = color)
    }
}

/** Gezackte Kante zwischen Foto und Papier. [color] ist die Farbe des Papiers darunter. */
@Composable
fun PerforationEdge(color: Color = KosBg, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(6.dp)) {
        val tooth = 7.dp.toPx()
        val gap = 5.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawRect(color, topLeft = Offset(x, 0f), size = Size(tooth, size.height))
            x += tooth + gap
        }
    }
}

/** Papier-Karte für Inhalte. */
@Composable
fun PaperPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(PostcardShape.Panel)
            .background(HomeCardColor)
            .border(1.dp, HomeCardBorder, PostcardShape.Panel)
            .padding(PostcardSpace.Panel),
        content = content
    )
}

/** Foto-Karte mit Titel und Untertitel unten auf dunklem Verlauf. */
@Composable
fun PostcardPhotoCard(
    @DrawableRes image: Int,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(128.dp).clip(PostcardShape.Photo)) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(PostcardMetrics.PHOTO_TEXT_PADDING_DP.dp)
        ) {
            Text(title, style = PostcardType.Heading.copy(fontSize = PostcardMetrics.BODY_SP.sp), color = Color.White)
            Text(subtitle, style = PostcardType.Label, color = Color.White.copy(alpha = 0.9f))
        }
    }
}
