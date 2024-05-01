package com.artemklymenko.cards.notification.utils

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.artemklymenko.cards.notification.NotificationScheduler
import com.artemklymenko.cards.notification.ReminderNotificationWorker
import com.artemklymenko.cards.utils.Constants
import com.artemklymenko.cards.vm.DataStorePreferenceManager

fun setupNotificationsSwitch(
    dataStorePreferenceManager: DataStorePreferenceManager,
    isChecked: Boolean,
    context: Context
) {
    val notificationScheduler = NotificationScheduler(context)
    dataStorePreferenceManager.notice = isChecked
    if (isChecked) {
        scheduleNotification(context)
    } else {
        notificationScheduler.cancelAll()
    }
}

private fun scheduleNotification(context: Context) {
    val notificationRequest = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
        .addTag(Constants.TAG_REMINDER_WORKER)
        .build()

    WorkManager.getInstance(context).enqueue(notificationRequest)
}