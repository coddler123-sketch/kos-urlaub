package com.example.kosurlaub.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.ui.theme.HomeAccent
import com.example.kosurlaub.ui.theme.KosBg
import com.example.kosurlaub.ui.theme.KosInk
import com.example.kosurlaub.ui.theme.KosOnAccent
import com.example.kosurlaub.ui.theme.KosSans

@Composable
fun UpdateBanner(
    update: AppUpdate,
    isDownloading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HomeAccent.copy(alpha = 0.22f))
            .clickable(enabled = !isDownloading, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isDownloading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = HomeAccent,
                    strokeWidth = 2.dp
                )
            }
            Text(
                text = if (isDownloading) "Lade Aktualisierung herunter …" else "Aktualisierung v${update.versionName} verfügbar · Tippe zum Installieren",
                color = KosInk,
                fontSize = 12.sp,
                fontFamily = KosSans,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StartupUpdateDialog(
    update: AppUpdate,
    isDownloading: Boolean,
    onDismiss: () -> Unit,
    onInstall: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isDownloading) onDismiss() },
        title = {
            Text(
                text = "Aktualisierung verfügbar",
                fontFamily = KosSans,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = KosInk
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Eine neue Version (v${update.versionName}) deiner Kos-App ist verfügbar. Jetzt installieren?",
                    fontFamily = KosSans,
                    fontSize = 14.sp,
                    color = KosInk.copy(alpha = 0.9f)
                )
                if (isDownloading) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = HomeAccent,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Wird heruntergeladen …",
                            fontFamily = KosSans,
                            fontSize = 13.sp,
                            color = KosInk.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (!isDownloading) onInstall() },
                enabled = !isDownloading,
                colors = ButtonDefaults.buttonColors(containerColor = HomeAccent, contentColor = KosOnAccent)
            ) {
                Text("Installieren", fontFamily = KosSans, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            if (!isDownloading) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = KosInk.copy(alpha = 0.14f), contentColor = KosInk)
                ) {
                    Text("Später", fontFamily = KosSans)
                }
            }
        },
        containerColor = KosBg
    )
}
