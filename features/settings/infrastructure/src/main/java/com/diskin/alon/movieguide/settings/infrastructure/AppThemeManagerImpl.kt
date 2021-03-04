package com.diskin.alon.movieguide.settings.infrastructure

import androidx.appcompat.app.AppCompatDelegate.*
import com.diskin.alon.movieguide.settings.appservices.data.AppTheme
import com.diskin.alon.movieguide.settings.appservices.interfaces.AppThemeManager
import javax.inject.Inject

class AppThemeManagerImpl @Inject constructor() : AppThemeManager {

    override fun setTheme(theme: AppTheme) {
        when(theme) {
            AppTheme.DARK -> setDefaultNightMode(MODE_NIGHT_YES)
            AppTheme.LIGHT -> setDefaultNightMode(MODE_NIGHT_NO)
        }
    }
}