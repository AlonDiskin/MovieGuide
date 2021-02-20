package com.diskin.alon.movieguide.news.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.news.appservices.data.ScheduleNewsNotificationRequest
import com.diskin.alon.movieguide.news.appservices.interfaces.NewsNotificationScheduler
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Coordinate app operations to schedule a user notification for unread published articles.
 */
class ScheduleNewsNotificationUseCase @Inject constructor(
    private val scheduler: NewsNotificationScheduler
) : UseCase<ScheduleNewsNotificationRequest,Completable>{

    override fun execute(param: ScheduleNewsNotificationRequest): Completable {
        return when(param.newsNotificationData.enabled) {
            true -> scheduler.schedule(param.newsNotificationData)
            else -> scheduler.cancel()
        }
    }
}