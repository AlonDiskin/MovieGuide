package com.diskin.alon.movieguide.settings.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.settings.appservices.data.ConfigNewsNotificationRequest
import com.diskin.alon.movieguide.settings.appservices.interfaces.NewsUpdateNotificationManager
import javax.inject.Inject

class ConfigNewsNotificationUseCase @Inject constructor(
    private val manager: NewsUpdateNotificationManager
) : UseCase<ConfigNewsNotificationRequest,Unit> {

    override fun execute(param: ConfigNewsNotificationRequest) {
        manager.config(param.config)
    }
}