package com.diskin.alon.movieguide.news.infrastructure

import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.common.util.messaging.NewsNotificationConfigEvent
import com.diskin.alon.movieguide.news.appservices.data.NewsNotificationData
import com.diskin.alon.movieguide.news.appservices.data.ScheduleNewsNotificationRequest
import javax.inject.Inject

class NotificationConfigEventMapper @Inject constructor()
    : Mapper<NewsNotificationConfigEvent, ScheduleNewsNotificationRequest> {

    override fun map(source: NewsNotificationConfigEvent): ScheduleNewsNotificationRequest {
        return ScheduleNewsNotificationRequest(
            NewsNotificationData(
                source.enabled,
                source.vibrate
            )
        )
    }
}