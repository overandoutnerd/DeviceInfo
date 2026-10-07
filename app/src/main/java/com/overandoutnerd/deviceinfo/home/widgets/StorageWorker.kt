package com.overandoutnerd.deviceinfo.home.widgets

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.datastore.preferences.core.*
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.time.Duration

class StorageWorker(
    private val context: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {

    companion object {

        private val uniqueWorkName = StorageWorker::class.java.simpleName

        @RequiresApi(Build.VERSION_CODES.O)
        fun enqueue(context: Context, force: Boolean = false) {

            val manager = WorkManager.getInstance(context)

            val requestBuilder = PeriodicWorkRequestBuilder<StorageWorker>(
                Duration.ofMinutes(30)
            )

            var workPolicy = ExistingPeriodicWorkPolicy.KEEP

            if(force) {
                workPolicy = ExistingPeriodicWorkPolicy.REPLACE
            }

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )

            // Immediate update
            val oneTimeRequest = OneTimeWorkRequestBuilder<StorageWorker>().build()
            manager.enqueueUniqueWork(
                uniqueWorkName + "_one_time",
                ExistingWorkPolicy.REPLACE,
                oneTimeRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }

    }

    override suspend fun doWork(): Result {

        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(StorageWidget::class.java)

        return try {
            setWidgetState(glanceIds, StorageInfo.Loading)

            setWidgetState(glanceIds, StorageRepo.getStorageInfo(context))

            Result.success()
        } catch (e: Exception) {
            setWidgetState(glanceIds, StorageInfo.Unavailable(e.message.orEmpty()))
            if(runAttemptCount < 10) {
                Result.retry()
            } else {
                Result.failure()
            }
        }

    }

    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: StorageInfo) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                glanceId = glanceId,
                updateState = { prefs ->
                    prefs.toMutablePreferences().apply {
                        when (newState) {
                            is StorageInfo.Available -> {
                                this[StorageInfo.statusKey] = "Available"
                                this[StorageInfo.totalKey] = newState.total
                                this[StorageInfo.freeKey] = newState.free
                                this[StorageInfo.progressKey] = newState.progress
                                this[StorageInfo.valueKey] = newState.value
                            }

                            is StorageInfo.Unavailable -> {
                                this[StorageInfo.statusKey] = "Unavailable"
                                this[StorageInfo.messageKey] = newState.message
                            }

                            StorageInfo.Loading -> {
                                this[StorageInfo.statusKey] = "Loading"
                            }
                        }
                    }
                }
            )
        }
        StorageWidget().updateAll(context)
    }

}