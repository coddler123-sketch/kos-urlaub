package com.example.kosurlaub

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.WorkInfo
import androidx.work.WorkManager
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class KosWidgetLifecycleTest {

    @Test
    fun activeWidgetRepairsPeriodicWork() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(KosWidgetWorker.PERIODIC_WORK).result.get(5, TimeUnit.SECONDS)
        workManager.pruneWork().result.get(5, TimeUnit.SECONDS)

        KosWidgetWorker.ensureScheduled(context, hasWidgets = true)

        waitForPeriodicWork(workManager) { it.state == WorkInfo.State.ENQUEUED }
        KosWidgetWorker.cancelAll(context)
    }

    @Test
    fun disablingLastWidgetCancelsPeriodicWork() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(KosWidgetWorker.PERIODIC_WORK).result.get(5, TimeUnit.SECONDS)
        workManager.pruneWork().result.get(5, TimeUnit.SECONDS)

        KosWidgetWorker.schedulePeriodic(context)
        waitForPeriodicWork(workManager) { it.state == WorkInfo.State.ENQUEUED }

        KosCountdownLargeWidgetProvider().onDisabled(context)

        waitForPeriodicWork(workManager) { it.state == WorkInfo.State.CANCELLED }
    }

    @Test
    fun missingWidgetCancelsRetainedPeriodicWork() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(KosWidgetWorker.PERIODIC_WORK).result.get(5, TimeUnit.SECONDS)
        workManager.pruneWork().result.get(5, TimeUnit.SECONDS)

        KosWidgetWorker.schedulePeriodic(context)
        waitForPeriodicWork(workManager) { it.state == WorkInfo.State.ENQUEUED }

        KosWidgetWorker.ensureScheduled(context, hasWidgets = false)

        waitForPeriodicWork(workManager) { it.state == WorkInfo.State.CANCELLED }
    }

    private fun waitForPeriodicWork(
        workManager: WorkManager,
        predicate: (WorkInfo) -> Boolean
    ) {
        repeat(50) {
            val work = workManager
                .getWorkInfosForUniqueWork(KosWidgetWorker.PERIODIC_WORK)
                .get(5, TimeUnit.SECONDS)
                .lastOrNull()
            if (work != null && predicate(work)) return
            Thread.sleep(100)
        }
        fail("Periodic widget work did not reach the expected state")
    }
}
