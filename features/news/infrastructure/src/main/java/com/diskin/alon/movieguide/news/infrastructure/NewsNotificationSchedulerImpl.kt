package com.diskin.alon.movieguide.news.infrastructure

import androidx.work.*
import com.diskin.alon.movieguide.news.appservices.data.NewsNotificationData
import com.diskin.alon.movieguide.news.appservices.interfaces.NewsNotificationScheduler
import io.reactivex.Completable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Coordinate the operation of the news update notification service.
 */
class NewsNotificationSchedulerImpl @Inject constructor(
    private val workManager: WorkManager
) : NewsNotificationScheduler {

    companion object {
        const val NEWS_NOTIFICATION_WORK_NAME = "news notification work"
        const val WORK_INTERVAL_MINUTES = 30L
    }

    override fun schedule(data: NewsNotificationData): Completable {
        return Completable.fromAction {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val workRequest = PeriodicWorkRequestBuilder<NewsNotificationWorker>(
                WORK_INTERVAL_MINUTES,
                TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(mapEventToWorkData(data))
                .build()

            workManager.enqueueUniquePeriodicWork(
                NEWS_NOTIFICATION_WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest)
        }
    }

    private fun mapEventToWorkData(data: NewsNotificationData): Data {
        return Data.Builder()
            .putBoolean(NewsNotificationWorker.KEY_VIBRATION,data.vibrate)
            .build()
    }

    override fun cancel(): Completable {
        return Completable.fromAction{
            workManager.cancelUniqueWork(NEWS_NOTIFICATION_WORK_NAME)
        }
    }
}