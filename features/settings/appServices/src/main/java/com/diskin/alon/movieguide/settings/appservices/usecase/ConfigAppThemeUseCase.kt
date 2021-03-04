package com.diskin.alon.movieguide.settings.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.settings.appservices.data.ConfigAppThemeRequest
import com.diskin.alon.movieguide.settings.appservices.interfaces.AppThemeManager
import javax.inject.Inject

class ConfigAppThemeUseCase @Inject constructor(
    private val themeManager: AppThemeManager
) : UseCase<ConfigAppThemeRequest,Unit>{

    override fun execute(param: ConfigAppThemeRequest) {
        return themeManager.setTheme(param.theme)
    }
}
