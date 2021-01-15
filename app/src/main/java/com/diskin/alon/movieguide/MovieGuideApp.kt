package com.diskin.alon.movieguide

import android.app.Application
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MovieGuideApp : Application() {

    override fun onCreate() {
        super.onCreate()

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
}