package com.diskin.alon.movieguide.news.appservices.interfaces

import com.diskin.alon.movieguide.news.appservices.data.NewsNotificationData
import io.reactivex.Completable

interface NewsNotificationScheduler {

    fun schedule(data: NewsNotificationData): Completable

    fun cancel(): Completable
}