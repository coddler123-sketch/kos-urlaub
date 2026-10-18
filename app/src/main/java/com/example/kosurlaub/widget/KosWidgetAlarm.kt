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

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Doze-toleranter Ausloeser fuer den Widget-Refresh.
 *
 * WorkManagers periodische Arbeit ist nur ein "irgendwann"-Versprechen: sobald das Geraet im Doze
 * ist (oder MIUI die App einschlaefert), koennen Stunden ohne Lauf vergehen — dann steht der
 * Countdown sichtbar falsch. setAndAllowWhileIdle feuert auch in den Doze-Wartungsfenstern und ist
 * damit das Naechste an einer Garantie, das eine normale App bekommt. Der Alarm bewaffnet sich bei
 * jedem Feuern selbst neu (setAndAllowWhileIdle kennt nur One-Shots), und der 15-Minuten-Worker
 * bewaffnet ihn ebenfalls neu — so kommt die Kette auch nach einem Neustart wieder in Gang.
 */
object KosWidgetAlarm {

    private const val INTERVAL_MS = 15 * 60 * 1000L

    fun schedule(context: Context) {
        val manager = context.getSystemService(AlarmManager::class.java) ?: return
        manager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + INTERVAL_MS,
            pendingIntent(context),
        )
    }

    fun cancel(context: Context) {
        context.getSystemService(AlarmManager::class.java)?.cancel(pendingIntent(context))
    }

    private fun pendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, KosWidgetAlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}

class KosWidgetAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        KosCountdownLargeWidgetProvider.updateAllLargeWidgets(context)
        KosWidgetWorker.runOnce(context)
        KosWidgetAlarm.schedule(context)
    }
}
