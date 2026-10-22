package com.example.kosurlaub.ui.home.today

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.ui.theme.KosInk
import com.example.kosurlaub.ui.theme.PaperPanel
import com.example.kosurlaub.ui.theme.PostcardLabel
import com.example.kosurlaub.ui.theme.PostcardType

/** Spruch des Tages groß und gut sichtbar, direkt unter dem Foto. */
@Composable
internal fun PhraseCard(phrase: String) {
    PaperPanel(Modifier.testTag("home-phrase")) {
        PostcardLabel("Spruch des Tages")
        Text(
            phrase,
            style = PostcardType.Quote.copy(fontSize = 22.sp, lineHeight = 30.sp),
            color = KosInk
        )
    }
}
