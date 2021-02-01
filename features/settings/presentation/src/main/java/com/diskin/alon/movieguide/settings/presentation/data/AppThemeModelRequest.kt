package com.diskin.alon.movieguide.settings.presentation.data

import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.settings.appservices.data.AppTheme
import com.diskin.alon.movieguide.settings.appservices.data.ConfigAppThemeRequest

data class AppThemeModelRequest(
    val theme: AppTheme
) : ModelRequest<ConfigAppThemeRequest,Unit>(ConfigAppThemeRequest(theme))