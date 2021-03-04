package com.diskin.alon.movieguide.news.di.common

import android.app.Application
import androidx.work.WorkManager
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.common.util.messaging.NewsNotificationConfigEvent
import com.diskin.alon.movieguide.news.appservices.data.ScheduleNewsNotificationRequest
import com.diskin.alon.movieguide.news.appservices.interfaces.NewsNotificationScheduler
import com.diskin.alon.movieguide.news.infrastructure.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NewsInfrastructureModule {

    companion object {

        @Provides
        fun provideWorkManager(app: Application): WorkManager {
            return WorkManager.getInstance(app)
        }
    }

    @Singleton
    @Binds
    abstract fun bindNotificationScheduler(scheduler: NewsNotificationSchedulerImpl): NewsNotificationScheduler

    @Singleton
    @Binds
    abstract fun bindRemoteRecentDateProvider(provider: RemoteRecentDateProviderImpl): RemoteRecentDateProvider

    @Singleton
    @Binds
    abstract fun bindLocalRecentDateProvider(provider: LocalRecentDateProviderImpl): LocalRecentDateProvider

    @Singleton
    @Binds
    abstract fun bindNotificationConfigEventMapper(mapper: NotificationConfigEventMapper): Mapper<NewsNotificationConfigEvent, ScheduleNewsNotificationRequest>

}