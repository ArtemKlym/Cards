package com.artemklymenko.cards.notification

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.artemklymenko.cards.utils.Constants.TAG_REMINDER_WORKER
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleReminderNotification() {
        Log.d("NotificationScheduler","Scheduling reminder notification.")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()

        val notificationRequest =
            PeriodicWorkRequestBuilder<ReminderNotificationWorker>(8, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(TAG_REMINDER_WORKER)
                .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "reminder_notification_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                notificationRequest
            )
    }

    fun cancelAll() {
        WorkManager.getInstance(context).cancelAllWorkByTag(TAG_REMINDER_WORKER)
    }
}


