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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Stellt die Widget-Zeitplanung nach Neustart und App-Update wieder her. */
class KosWidgetScheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            KosWidgetWorker.ensureScheduled(context, requestRefresh = true)
        }
    }
}
