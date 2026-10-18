package com.example.kosurlaub.ui.home

import android.content.Context
import com.example.kosurlaub.BuildConfig
import java.util.Calendar
import java.util.TimeZone

/**
 * Zeitquelle der App. Im Release-Build immer die Systemzeit; im Debug-Build kann ein Testdatum
 * als Versatz gesetzt werden (Einstellungen), damit alle Reisephasen ohne Umstellen der Geräteuhr prüfbar sind.
 */
object AppClock {
    private const val PREFS = "kos_debug"
    private const val KEY_OFFSET = "clock_offset_millis"

    @Volatile
    private var offsetMillis: Long = 0L

    fun nowMillis(): Long = System.currentTimeMillis() + offsetMillis

    fun calendar(): Calendar = Calendar.getInstance().apply { timeInMillis = nowMillis() }

    /** Beim Start im Debug-Build den gespeicherten Versatz laden. */
    fun init(context: Context) {
        offsetMillis = if (BuildConfig.DEBUG) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(KEY_OFFSET, 0L)
        } else 0L
    }

    /** Setzt „jetzt“ auf [targetMillis]; null stellt die echte Zeit wieder her. Nur im Debug-Build wirksam. */
    fun setTestTime(context: Context, targetMillis: Long?) {
        if (!BuildConfig.DEBUG) return
        offsetMillis = targetMillis?.let { it - System.currentTimeMillis() } ?: 0L
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putLong(KEY_OFFSET, offsetMillis).apply()
    }

    val isOverridden: Boolean get() = offsetMillis != 0L

    fun millisOf(zone: String, y: Int, m: Int, d: Int, h: Int, min: Int): Long =
        Calendar.getInstance(TimeZone.getTimeZone(zone)).apply { clear(); set(y, m - 1, d, h, min, 0) }.timeInMillis
}
