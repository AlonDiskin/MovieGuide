package com.diskin.alon.movieguide

import android.app.Application
import android.content.Intent
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationConfigListenerService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MovieGuideApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        // Start news notification config listener service
        startService(Intent(this,NewsNotificationConfigListenerService::class.java))

        // Restore night mode according to app preference
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val themeKey = getString(R.string.pref_theme_key)
        val themeDefault = getString(R.string.pref_theme_default_value)

        when(sp.getString(themeKey,themeDefault)!!) {
            getString(R.string.pref_theme_day_value) ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            getString(R.string.pref_theme_night_value) ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}