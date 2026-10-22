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
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Locale

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

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        updateLargeWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        WidgetStyle.forget(context, appWidgetIds)
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
            val heightDp = appWidgetManager.getAppWidgetOptions(appWidgetId)
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 0)
            val views = buildViews(context, WidgetStyle.of(context, appWidgetId), heightDp)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        /** Baut die Widget-Ansicht im gewaehlten Stil; [heightDp] = 0 heisst unbekannt. */
        fun buildViews(context: Context, style: WidgetStyle, heightDp: Int = 0): RemoteViews {
            val remaining = calculateRemainingTime()
            val views = RemoteViews(context.packageName, style.layout)
            views.setTextViewText(R.id.widget_phrase_value, phraseOfTheDay(context))
            if (style == WidgetStyle.ISLAND) {
                bindIsland(context, views, remaining)
            } else {
                bindStyled(context, views, style, remaining, heightDp)
            }
            applyWeather(context, views)

            views.setOnClickPendingIntent(R.id.widget_root, openAppIntent(context))
            views.setOnClickPendingIntent(R.id.widget_large_title, refreshIntent(context))
            views.setOnClickPendingIntent(R.id.widget_weather_pill, refreshIntent(context))
            return views
        }

        private fun bindIsland(context: Context, views: RemoteViews, remaining: RemainingTime) {
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
                        WidgetPhaseText.vacationDay(AppClock.nowMillis()) ?: context.getString(R.string.widget_vacation_line1))
                    applyDayImage(context, views)
                }
                remaining.isReached -> {
                    views.setViewVisibility(R.id.widget_countdown_row, View.GONE)
                    views.setViewVisibility(R.id.widget_progress_bar, View.GONE)
                    views.setViewVisibility(R.id.widget_arrival_message, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_vacation_subtitle, View.GONE)
                    views.setViewVisibility(R.id.widget_phrase_container, View.GONE)
                    views.setTextViewText(R.id.widget_arrival_message, WidgetPhaseText.departureDay())
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
        }

        /**
         * Gemeinsame Bindung fuer Abflugtafel, Glas, Kachel und Kompakt. Jedes Layout
         * enthaelt nur die IDs, die es braucht; RemoteViews ignoriert fehlende IDs.
         */
        private fun bindStyled(
            context: Context,
            views: RemoteViews,
            style: WidgetStyle,
            remaining: RemainingTime,
            heightDp: Int
        ) {
            val showMessage = remaining.isOver || remaining.isOnVacation || remaining.isReached
            views.setViewVisibility(R.id.widget_countdown_content, if (showMessage) View.GONE else View.VISIBLE)
            views.setViewVisibility(R.id.widget_message_content, if (showMessage) View.VISIBLE else View.GONE)
            views.setViewVisibility(R.id.widget_vacation_subtitle, if (remaining.isOnVacation) View.VISIBLE else View.GONE)

            val phraseFits = style != WidgetStyle.BOARD || heightDp == 0 || heightDp >= BOARD_PHRASE_MIN_HEIGHT_DP
            views.setViewVisibility(
                R.id.widget_phrase_container,
                if (!showMessage && phraseFits && shouldShowWidgetPhrase(context.resources.configuration.fontScale)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            )

            when {
                remaining.isOver -> {
                    views.setTextViewText(R.id.widget_arrival_message, context.getString(R.string.widget_over_message))
                    applyDayImage(context, views)
                }
                remaining.isOnVacation -> {
                    views.setTextViewText(
                        R.id.widget_arrival_message,
                        WidgetPhaseText.vacationDay(AppClock.nowMillis()) ?: context.getString(R.string.widget_vacation_line1)
                    )
                    applyDayImage(context, views)
                }
                remaining.isReached -> {
                    views.setTextViewText(R.id.widget_arrival_message, WidgetPhaseText.departureDay())
                    views.setImageViewResource(R.id.widget_background_image, ARRIVAL_BACKGROUND)
                }
                else -> {
                    bindCountdownTexts(context, views, style, remaining)
                    applyDayImage(context, views)
                }
            }
        }

        private fun bindCountdownTexts(context: Context, views: RemoteViews, style: WidgetStyle, remaining: RemainingTime) {
            val days = remaining.days.toInt()
            val hours = remaining.hours.toString().padStart(2, '0')
            val minutes = remaining.minutes.toString().padStart(2, '0')
            val res = context.resources
            val flight = TripData.outbound

            views.setTextViewText(R.id.widget_days, days.toString())
            views.setTextViewText(R.id.widget_hours, hours)
            views.setTextViewText(R.id.widget_minutes, minutes)
            views.setTextViewText(R.id.widget_hm_text, context.getString(R.string.widget_hm_format, hours, minutes))
            views.setTextViewText(R.id.widget_days_headline, res.getQuantityString(R.plurals.widget_days_remaining, days, days))
            val unitPlural = if (style == WidgetStyle.POSTCARD) R.plurals.widget_days_until else R.plurals.widget_days_unit
            views.setTextViewText(R.id.widget_days_unit, res.getQuantityString(unitPlural, days))

            val digits = days.coerceAtMost(999).toString().padStart(3, '0')
            views.setTextViewText(R.id.widget_digit_1, digits[0].toString())
            views.setTextViewText(R.id.widget_digit_2, digits[1].toString())
            views.setTextViewText(R.id.widget_digit_3, digits[2].toString())

            views.setTextViewText(R.id.widget_route, "${flight.fromCity} → ${flight.toCity}".uppercase(Locale.GERMAN))
            views.setTextViewText(
                R.id.widget_flight_sub,
                context.getString(R.string.widget_board_subtitle, flight.number.replace(Regex("^([A-Z]+)(\\d+)$"), "$1 $2"))
            )
            views.setTextViewText(R.id.widget_from_code, flight.fromAirport)
            views.setTextViewText(R.id.widget_to_code, flight.toAirport)

            val departure = CountdownCalculator.departureCalendar()
            val dateLabel = SimpleDateFormat("d. MMMM", Locale.GERMAN)
                .apply { timeZone = departure.timeZone }
                .format(departure.time)
            views.setTextViewText(R.id.widget_date_text, dateLabel)
            views.setTextViewText(R.id.widget_glass_departure, context.getString(R.string.widget_glass_departure, dateLabel))

            val progress = widgetProgressValue(CountdownCalculator.progressFraction())
            views.setProgressBar(R.id.widget_progress_bar, 1000, progress, false)
            bindPlane(views, progress)
        }

        /** Zeigt das Flugzeug an der Stelle des Fortschritts; RemoteViews kennen keine dynamische Position, daher 21 feste Plätze. */
        private fun bindPlane(views: RemoteViews, progress: Int) {
            val active = progress * (PLANE_IDS.size - 1) / 1000
            PLANE_IDS.forEachIndexed { i, id ->
                views.setViewVisibility(id, if (i == active) View.VISIBLE else View.INVISIBLE)
            }
        }

        private val PLANE_IDS = intArrayOf(
            R.id.widget_plane_0,
            R.id.widget_plane_1,
            R.id.widget_plane_2,
            R.id.widget_plane_3,
            R.id.widget_plane_4,
            R.id.widget_plane_5,
            R.id.widget_plane_6,
            R.id.widget_plane_7,
            R.id.widget_plane_8,
            R.id.widget_plane_9,
            R.id.widget_plane_10,
            R.id.widget_plane_11,
            R.id.widget_plane_12,
            R.id.widget_plane_13,
            R.id.widget_plane_14,
            R.id.widget_plane_15,
            R.id.widget_plane_16,
            R.id.widget_plane_17,
            R.id.widget_plane_18,
            R.id.widget_plane_19,
            R.id.widget_plane_20
        )

        private const val BOARD_PHRASE_MIN_HEIGHT_DP = 140

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
                localEpochDay(),
                context.resources.getStringArray(R.array.widget_phrases_far)
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
