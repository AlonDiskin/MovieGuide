package com.diskin.alon.movieguide.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.settings.appservices.data.AppTheme
import com.diskin.alon.movieguide.settings.appservices.data.NewsNotificationConfig
import com.diskin.alon.movieguide.settings.presentation.data.AppThemeModelRequest
import com.diskin.alon.movieguide.settings.presentation.data.ConfigNewsNotificationModelRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val model: Model
) : ViewModel() {

    fun setAppTheme(theme: AppTheme) {
        model.execute(AppThemeModelRequest(theme))
    }

    fun configNewsUpdateNotification(config: NewsNotificationConfig) {
        model.execute(ConfigNewsNotificationModelRequest(config))
    }
}