package com.diskin.alon.movieguide.settings.appservices.interfaces

import com.diskin.alon.movieguide.settings.appservices.data.NewsNotificationConfig

interface NewsUpdateNotificationManager {

    fun config(configuration: NewsNotificationConfig)
}