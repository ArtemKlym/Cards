package com.artemklymenko.cards.notification

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.artemklymenko.cards.utils.Constants

class ReminderNotificationWorker(
    private val appContext: Context, workerParameters: WorkerParameters
) : Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        return try{
            NotificationHandler.createReminderNotification(appContext)
            Result.success()
        }catch (ex: Exception){
            Log.e(Constants.TAG_WORK_MANAGER, "Exception $ex")
            Result.failure()
        }
    }
}