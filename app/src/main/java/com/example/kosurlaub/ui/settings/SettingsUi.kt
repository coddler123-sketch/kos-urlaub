package com.example.kosurlaub.ui.settings
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.unit.sp
import com.example.kosurlaub.BuildConfig
import com.example.kosurlaub.ui.home.AppClock
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsSheet(
    themeMode: ThemeMode,
    onThemeMode: (ThemeMode) -> Unit,
    isCheckingUpdate: Boolean,
    hasUpdate: Boolean,
    onCheckUpdate: () -> Unit,
    onShare: () -> Unit,
    onOpenGallery: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Optionen",
            color = KosInk.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = KosSans,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "◐", fontSize = 16.sp, modifier = Modifier.width(28.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Darstellung", color = KosInk, fontSize = 14.sp, fontFamily = KosSans, fontWeight = FontWeight.SemiBold)
                Text(text = "System, hell oder dunkel", color = KosInk.copy(alpha = 0.6f), fontSize = 11.sp, fontFamily = KosSans)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeMode.entries.forEach { mode ->
                SecondaryActionButton(
                    text = mode.label,
                    enabled = true,
                    onClick = { onThemeMode(mode) },
                    modifier = Modifier.testTag("settings-theme-${mode.name.lowercase()}"),
                    containerColor = if (mode == themeMode) HomeAccent else KosInk.copy(alpha = 0.14f),
                    contentColor = if (mode == themeMode) KosOnAccent else KosInk
                )
            }
        }
        if (BuildConfig.DEBUG) DebugClockSection()
        SettingsRow(
            icon = "🖼",
            label = "Titelbild",
            detail = "Vorschau und Auswahl",
            actionLabel = "Galerie",
            onAction = onOpenGallery,
            actionTag = "settings-open-gallery"
        )
        SettingsRow(
            icon = if (hasUpdate) "●" else "✓",
            label = "Version v${BuildConfig.VERSION_NAME}",
            detail = if (hasUpdate) "Aktualisierung verfügbar" else "Aktuellste Version",
            actionLabel = when {
                isCheckingUpdate -> "Lädt …"
                hasUpdate -> "Installieren"
                else -> "Prüfen"
            },
            onAction = onCheckUpdate,
            enabled = !isCheckingUpdate,
            actionTag = "settings-check-update"
        )
        SettingsRow(
            icon = "↗",
            label = "Urlaubsvorfreude teilen",
            detail = "Über WhatsApp, SMS, …",
            actionLabel = "Teilen",
            onAction = onShare,
            actionTag = "settings-share"
        )
    }
}

@Composable
private fun SettingsRow(
    icon: String,
    label: String,
    detail: String,
    actionLabel: String,
    onAction: () -> Unit,
    enabled: Boolean = true,
    actionTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 16.sp, modifier = Modifier.width(28.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = KosInk, fontSize = 14.sp, fontFamily = KosSans, fontWeight = FontWeight.SemiBold)
            Text(text = detail, color = KosInk.copy(alpha = 0.6f), fontSize = 11.sp, fontFamily = KosSans)
        }
        SecondaryActionButton(
            text = actionLabel,
            enabled = enabled,
            onClick = onAction,
            modifier = Modifier.testTag(actionTag)
        )
    }
}

@Composable
private fun SecondaryActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = KosInk.copy(alpha = 0.14f),
    contentColor: Color = KosInk
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(32.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = KosInk.copy(alpha = 0.09f),
            disabledContentColor = KosInk.copy(alpha = 0.6f)
        )
    ) {
        Text(
            text = text,
            fontFamily = KosSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        )
    }
}

/** Nur im Debug-Build: Testdatum für die Reisephasen. Startet die Activity neu, damit alle Zustände neu berechnet werden. */
@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun DebugClockSection() {
    val context = LocalContext.current
    val presets = listOf(
        "Echte Zeit" to null,
        "Vor Abflug (12.09.)" to AppClock.millisOf("Europe/Berlin", 2027, 9, 12, 10, 0),
        "Auf Kos (16.09.)" to AppClock.millisOf("Europe/Athens", 2027, 9, 16, 12, 0),
        "Nach Rückflug (29.09.)" to AppClock.millisOf("Europe/Athens", 2027, 9, 29, 10, 0)
    )
    Text(
        text = "TESTDATUM (NUR DEBUG)" + if (AppClock.isOverridden) " · aktiv" else "",
        color = KosInk.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        fontFamily = KosSans, letterSpacing = 1.sp, modifier = Modifier.testTag("debug-clock-label")
    )
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEachIndexed { index, (label, millis) ->
            SecondaryActionButton(
                text = label,
                enabled = true,
                onClick = {
                    AppClock.setTestTime(context, millis)
                    generateSequence(context) { (it as? android.content.ContextWrapper)?.baseContext }
                        .filterIsInstance<android.app.Activity>().firstOrNull()?.recreate()
                },
                modifier = Modifier.testTag("debug-clock-$index")
            )
        }
    }
}
