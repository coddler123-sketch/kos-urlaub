package com.example.kosurlaub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kosurlaub.ui.theme.*

/** Nur im Debug-Build: zeigt alle Postkarten-Bausteine zur Sichtprüfung. */
class DesignGalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KosWidgetTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(KosBg)
                        .systemBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(PostcardSpace.Screen),
                    verticalArrangement = Arrangement.spacedBy(PostcardSpace.Gap)
                ) {
                    Text("Schön war es.", style = PostcardType.Title, color = KosInk)
                    Text("Sonnenuntergang in Tigaki, die Füße im Wasser.", style = PostcardType.Quote, color = KosInk)
                    Row(horizontalArrangement = Arrangement.spacedBy(PostcardSpace.Gap), verticalAlignment = Alignment.CenterVertically) {
                        Text("330", style = PostcardType.Numeral, color = KosNumeral)
                        StampChip("Tage")
                        StampBadge("TAG", "4/16")
                    }
                    Box(modifier = Modifier.height(90.dp)) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(R.drawable.kos_rot_11),
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        PerforationEdge(modifier = Modifier.align(Alignment.BottomCenter))
                    }
                    PaperPanel {
                        PostcardLabel("Flug & Hotel")
                        Text("Mo 13.09. · 13:25 HAM → 17:45 KGS", style = PostcardType.Body, color = KosInk)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(PostcardSpace.Gap)) {
                        PostcardPhotoCard(R.drawable.excursion_old_town, "Altstadt", "Ausflug", Modifier.weight(1f))
                        PostcardPhotoCard(R.drawable.excursion_marmari, "Marmari", "Strand", Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
