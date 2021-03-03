package com.diskin.alon.movieguide.news.infrastructure

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.common.util.messaging.NewsNotificationConfigEvent
import com.diskin.alon.movieguide.news.appservices.data.ScheduleNewsNotificationRequest
import com.diskin.alon.movieguide.news.appservices.usecase.ScheduleNewsNotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * This service exist in the scope of the app lifetime,listening for [NewsNotificationConfigEvent]events.
 * Once an event has been received, service will delegate the configuration of news notification to
 * capable collaborators.
 */
@OptionalInject
@AndroidEntryPoint
class NewsNotificationConfigListenerService : Service() {

    @Inject
    lateinit var useCase: ScheduleNewsNotificationUseCase
    @Inject
    lateinit var mapper: Mapper<NewsNotificationConfigEvent,ScheduleNewsNotificationRequest>

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @SuppressLint("CheckResult")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NewsNotificationConfigEvent) {
        useCase.execute(mapper.map(event))
            .subscribe()
    }
}