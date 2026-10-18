package com.example.kosurlaub.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kosurlaub.R
import com.example.kosurlaub.ui.theme.KosWidgetTheme

/**
 * Stil-Auswahl fuer das 4x2-Widget: erscheint beim Hinzufuegen und (ab Android 9)
 * beim erneuten Konfigurieren per langem Druck. Die Vorschauen sind die echten
 * Widget-Ansichten.
 */
class KosWidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        // Zurueck-Taste = abbrechen, das Widget wird dann nicht platziert.
        setResult(RESULT_CANCELED, resultIntent(appWidgetId))
        val forAll = intent.getBooleanExtra(EXTRA_ALL_WIDGETS, false)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !forAll) {
            finish()
            return
        }
        val current = (if (forAll) placedWidgetIds().firstOrNull() else appWidgetId)
            ?.let { WidgetStyle.of(this, it) } ?: WidgetStyle.ISLAND
        setContent {
            KosWidgetTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    StylePicker(current) { choose(if (forAll) placedWidgetIds() else listOf(appWidgetId), it) }
                }
            }
        }
    }

    private fun placedWidgetIds(): List<Int> = AppWidgetManager.getInstance(this)
        .getAppWidgetIds(ComponentName(this, KosCountdownLargeWidgetProvider::class.java)).toList()

    private fun choose(appWidgetIds: List<Int>, style: WidgetStyle) {
        val manager = AppWidgetManager.getInstance(this)
        appWidgetIds.forEach {
            WidgetStyle.save(this, it, style)
            KosCountdownLargeWidgetProvider.updateLargeWidget(this, manager, it)
        }
        KosWidgetWorker.ensureScheduled(this, requestRefresh = true)
        setResult(RESULT_OK, resultIntent(appWidgetIds.firstOrNull() ?: AppWidgetManager.INVALID_APPWIDGET_ID))
        finish()
    }

    companion object {
        /** Stil fuer alle platzierten Widgets setzen (Aufruf aus den App-Optionen). */
        const val EXTRA_ALL_WIDGETS = "all_widgets"
    }

    private fun resultIntent(appWidgetId: Int) =
        Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
}

private val PreviewHeight = 150.dp

@Composable
private fun StylePicker(current: WidgetStyle, onPick: (WidgetStyle) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(stringResource(R.string.widget_config_title), style = MaterialTheme.typography.headlineSmall)
                Text(
                    stringResource(R.string.widget_config_hint),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        items(WidgetStyle.values().toList()) { style ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = if (style == current) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().height(PreviewHeight).clip(RoundedCornerShape(24.dp))) {
                        WidgetPreview(style)
                        // faengt Taps ab, damit die Klick-Intents der Vorschau nicht feuern
                        Box(modifier = Modifier.fillMaxSize().clickable { onPick(style) })
                    }
                    Text(
                        stringResource(style.nameRes),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(stringResource(style.descriptionRes), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun WidgetPreview(style: WidgetStyle) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            FrameLayout(ctx).also { frame ->
                val views = KosCountdownLargeWidgetProvider.buildViews(ctx, style, PreviewHeight.value.toInt())
                frame.addView(views.apply(ctx, frame))
            }
        }
    )
}
