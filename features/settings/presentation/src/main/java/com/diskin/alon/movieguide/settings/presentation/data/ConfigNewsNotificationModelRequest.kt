package com.diskin.alon.movieguide.settings.presentation.data

import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.settings.appservices.data.ConfigNewsNotificationRequest
import com.diskin.alon.movieguide.settings.appservices.data.NewsNotificationConfig

data class ConfigNewsNotificationModelRequest(
    val config: NewsNotificationConfig
) : ModelRequest<ConfigNewsNotificationRequest,Unit>(ConfigNewsNotificationRequest(config))