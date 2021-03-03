package com.diskin.alon.movieguide

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import com.diskin.alon.movieguide.home.presentation.MainActivity
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationConfigListenerService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MovieGuideApp : Application(), Configuration.Provider,
    Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Register activities listener for services lifecycle management
        registerActivityLifecycleCallbacks(this)

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

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        if(activity is MainActivity) {
            startService(Intent(
                this,
                NewsNotificationConfigListenerService::class.java)
            )
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        if(activity is MainActivity) {
            stopService(Intent(
                this,
                NewsNotificationConfigListenerService::class.java)
            )
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}