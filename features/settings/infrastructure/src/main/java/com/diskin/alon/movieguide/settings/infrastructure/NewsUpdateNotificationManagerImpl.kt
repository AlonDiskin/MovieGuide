package com.diskin.alon.movieguide.settings.infrastructure

import com.diskin.alon.movieguide.common.util.messaging.NewsUpdateConfigEvent
import com.diskin.alon.movieguide.settings.appservices.data.NewsNotificationConfig
import com.diskin.alon.movieguide.settings.appservices.interfaces.NewsUpdateNotificationManager
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class NewsUpdateNotificationManagerImpl @Inject constructor(
    private val eventBus: EventBus
) : NewsUpdateNotificationManager {

    override fun config(configuration: NewsNotificationConfig) {
        val event = NewsUpdateConfigEvent(
            configuration.enabled,
            configuration.vibrate
        )

        eventBus.post(event)
    }
}