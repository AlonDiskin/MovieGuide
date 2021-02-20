package com.diskin.alon.movieguide.news.infrastructure

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.migration.OptionalInject
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

/**
 * Notifies user with a status bar notification, about new & unread available articles.
 */
@OptionalInject
@HiltWorker
class NewsNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val remoteProvider: RemoteRecentDateProvider,
    private val localProvider: LocalRecentDateProvider
) : RxWorker(context, workerParams) {

    companion object {
        const val KEY_VIBRATION = "vibration"
        const val CHANNEL_ID = "news notification channel id"
        const val NOTIFICATION_ID = 100
        val VIBRATION_PATTERN = longArrayOf(0, 500)
    }

    override fun createWork(): Single<Result> {
        return Observable.combineLatest(
            remoteProvider.getDate().toObservable(),
            localProvider.getDate().toObservable(),
            { remoteDate, localDate ->
                if (isUnreadArticlesPublished(remoteDate, localDate) && !isAppInForeground()) {
                    showStatusBarNotification()
                }

                Result.success()
            })
            .onErrorReturn { Result.failure() }
            .firstOrError()
    }

    private fun isUnreadArticlesPublished(remoteDate: Date, localDate: Date): Boolean {
        return remoteDate.time > localDate.time
    }

    private fun showStatusBarNotification() {
        val builder = NotificationCompat.Builder(
            applicationContext, CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(applicationContext.getString(R.string.notification_text))
            .setContentIntent(createNotificationContentIntent())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (inputData.getBoolean(KEY_VIBRATION, false)) {
            builder.setVibrate(VIBRATION_PATTERN)
        }

        createNotificationChannel()
        with(NotificationManagerCompat.from(applicationContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.channel_name)
            val descriptionText = applicationContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (inputData.getBoolean(KEY_VIBRATION, false)) {
                channel.enableVibration(true)
                channel.vibrationPattern = VIBRATION_PATTERN
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun isAppInForeground(): Boolean {
        val appProcessInfo = RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        

        return appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE
    }

    private fun createNotificationContentIntent(): PendingIntent {
        val appPackage = applicationContext.packageName
        val packageManager = applicationContext.packageManager
        val intent = packageManager.getLaunchIntentForPackage(appPackage)

        return PendingIntent.getActivity(applicationContext, 0, intent, 0)
    }
}