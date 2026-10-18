package com.example.kosurlaub.widget
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews

/**
 * 4x2-Widget: Countdown auf das fixe Abflugdatum, Hintergrundbild und Spruch
 * wechseln taeglich, dazu das aktuelle Kos-Wetter. Tap auf "KOS" loest
 * eine sofortige Aktualisierung aus; Tap auf den Rest oeffnet die App.
 */
class KosCountdownLargeWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id -> updateLargeWidget(context, appWidgetManager, id) }
        // 15-Minuten-Refresh sicherstellen (WorkManager plus Doze-toleranter Alarm als Rueckfall)
        // und einmal sofort frisch holen.
        KosWidgetWorker.ensureScheduled(
            context = context,
            requestRefresh = true
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            KosWidgetWorker.refreshNow(context)
        }
    }

    override fun onDisabled(context: Context) {
        KosWidgetAlarm.cancel(context)
        KosWidgetWorker.cancelAll(context)
        super.onDisabled(context)
    }

    companion object {
        const val ACTION_REFRESH = "com.example.kosurlaub.ACTION_REFRESH"

        private const val ARRIVAL_BACKGROUND = R.drawable.kos_rot_01

        fun updateLargeWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val remaining = calculateRemainingTime()
            val views = RemoteViews(context.packageName, R.layout.kos_countdown_widget_large)

            views.setTextViewText(R.id.widget_phrase_value, phraseOfTheDay(context))

            views.setViewVisibility(R.id.widget_subtitle, if (remaining.isReached) View.GONE else View.VISIBLE)
            when {
                remaining.isOver -> {
                    views.setViewVisibility(R.id.widget_countdown_row, View.GONE)
                    views.setViewVisibility(R.id.widget_progress_bar, View.GONE)
                    views.setViewVisibility(R.id.widget_arrival_message, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_vacation_subtitle, View.GONE)
                    views.setViewVisibility(R.id.widget_phrase_container, View.GONE)
                    views.setTextViewText(R.id.widget_arrival_message, context.getString(R.string.widget_over_message))
                    applyDayImage(context, views)
                }
                remaining.isOnVacation -> {
                    views.setViewVisibility(R.id.widget_countdown_row, View.GONE)
                    views.setViewVisibility(R.id.widget_progress_bar, View.GONE)
                    views.setViewVisibility(R.id.widget_arrival_message, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_vacation_subtitle, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_phrase_container, View.GONE)
                    views.setTextViewText(R.id.widget_arrival_message,
                        context.getString(R.string.widget_vacation_line1))
                    applyDayImage(context, views)
                }
                remaining.isReached -> {
                    views.setViewVisibility(R.id.widget_countdown_row, View.GONE)
                    views.setViewVisibility(R.id.widget_progress_bar, View.GONE)
                    views.setViewVisibility(R.id.widget_arrival_message, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_vacation_subtitle, View.GONE)
                    views.setViewVisibility(R.id.widget_phrase_container, View.GONE)
                    views.setTextViewText(R.id.widget_arrival_message,
                        context.getString(R.string.widget_arrival_message))
                    views.setImageViewResource(R.id.widget_background_image, ARRIVAL_BACKGROUND)
                }
                else -> {
                    views.setViewVisibility(R.id.widget_countdown_row, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_arrival_message, View.GONE)
                    views.setViewVisibility(R.id.widget_vacation_subtitle, View.GONE)
                    views.setViewVisibility(
                        R.id.widget_phrase_container,
                        if (shouldShowWidgetPhrase(context.resources.configuration.fontScale)) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    )
                    views.setTextViewText(R.id.widget_days, remaining.days.toString())
                    views.setTextViewText(R.id.widget_hours, remaining.hours.toString().padStart(2, '0'))
                    views.setTextViewText(R.id.widget_minutes, remaining.minutes.toString().padStart(2, '0'))
                    applyDayImage(context, views)
                    val progress = widgetProgressValue(CountdownCalculator.progressFraction())
                    views.setProgressBar(R.id.widget_progress_bar, 1000, progress, false)
                    views.setViewVisibility(R.id.widget_progress_bar, View.VISIBLE)
                }
            }
            applyWeather(context, views)

            views.setOnClickPendingIntent(R.id.widget_root, openAppIntent(context))
            views.setOnClickPendingIntent(R.id.widget_large_title, refreshIntent(context))
            views.setOnClickPendingIntent(R.id.widget_weather_pill, refreshIntent(context))
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        /** Aktualisiert alle platzierten 4x2-Widgets (z. B. nach einem Wetter-Abruf). */
        fun updateAllLargeWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, KosCountdownLargeWidgetProvider::class.java)
            )
            ids.forEach { id -> updateLargeWidget(context, manager, id) }
        }

        internal fun widgetProgressValue(progressFraction: Float): Int =
            (progressFraction.coerceIn(0f, 1f) * 1000).toInt()

        internal fun shouldShowWidgetPhrase(fontScale: Float): Boolean = fontScale < 1.3f

        /** Privates Tagesfoto (klein skaliert, RemoteViews-Limit), sonst freies Bild aus den Ressourcen. */
        private fun applyDayImage(context: Context, views: RemoteViews) {
            val bitmap = Images.privatePathOfTheDay(context)?.let { PrivateImages.bitmap(context, it, 900) }
            if (bitmap != null) {
                views.setImageViewBitmap(R.id.widget_background_image, bitmap)
                return
            }
            val image = Images.resourceOfTheDay(context)
            if (image != 0) views.setImageViewResource(R.id.widget_background_image, image)
        }

        private fun applyWeather(context: Context, views: RemoteViews) {
            val weather = WeatherRepository.cached(context) ?: return
            views.setTextViewText(R.id.widget_weather_temp, weather.temperatureLabel)
            views.setImageViewResource(
                R.id.widget_weather_icon,
                WeatherRepository.iconFor(weather)
            )
            // Veraltetes Wetter (Netzabruf > 6h her) leicht verblassen lassen.
            val ageMillis = System.currentTimeMillis() - WeatherRepository.lastFetchMillis(context)
            val alpha = if (ageMillis > STALE_AFTER_MILLIS) STALE_ALPHA else FRESH_ALPHA
            views.setFloat(R.id.widget_weather_icon, "setAlpha", alpha)
            views.setFloat(R.id.widget_weather_temp, "setAlpha", alpha)
        }

        private const val STALE_AFTER_MILLIS = 6L * 60 * 60 * 1000
        private const val FRESH_ALPHA = 1.0f
        private const val STALE_ALPHA = 0.5f

        private fun phraseOfTheDay(context: Context): String {
            countdownMilestone(CountdownCalculator.daysUntilDeparture().toLong())?.let {
                return it.message
            }
            return phraseOfTheDay(
                context.resources.getStringArray(R.array.widget_phrases),
                context.resources.getStringArray(R.array.widget_phrases_daily),
                CountdownCalculator.daysUntilDeparture(),
                localEpochDay()
            )
        }

        private fun openAppIntent(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun refreshIntent(context: Context): PendingIntent {
            val intent = Intent(context, KosCountdownLargeWidgetProvider::class.java)
                .setAction(ACTION_REFRESH)
            return PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun calculateRemainingTime() = CountdownCalculator.calculate()
    }
}
