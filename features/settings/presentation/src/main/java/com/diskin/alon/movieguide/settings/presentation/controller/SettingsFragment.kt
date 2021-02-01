package com.diskin.alon.movieguide.settings.presentation.controller

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import com.diskin.alon.movieguide.settings.appservices.data.AppTheme
import com.diskin.alon.movieguide.settings.appservices.data.NewsNotificationConfig
import com.diskin.alon.movieguide.settings.presentation.R
import com.diskin.alon.movieguide.settings.presentation.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

@OptionalInject
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModels()
    private val sharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            val appThemeKey = getString(R.string.pref_theme_key)
            val newsNotificationKey = getString(R.string.pref_news_notification_key)
            val newsNotificationVibrationKey = getString(R.string.pref_news_notification_vibration_key)

            when(key) {
                appThemeKey -> handleAppThemePrefChange()
                newsNotificationKey -> handleNewsUpdateNotificationPrefChange()
                newsNotificationVibrationKey -> handleNewsUpdateNotificationVibrationPrefChange()
            }
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    private fun handleAppThemePrefChange() {
        val prefs = preferenceScreen.sharedPreferences
        val appThemeKey = getString(R.string.pref_theme_key)
        val themeDefault = getString(R.string.pref_theme_default_value)
        if (prefs.getString(
                appThemeKey,
                themeDefault
            ) == getString(R.string.pref_theme_day_value)
        ) {
            viewModel.setAppTheme(AppTheme.LIGHT)
        } else {
            viewModel.setAppTheme(AppTheme.DARK)
        }
    }

    private fun handleNewsUpdateNotificationPrefChange() {
        val prefs = preferenceScreen.sharedPreferences
        val key = getString(R.string.pref_news_notification_key)
        val defaultValue = getString(R.string.pref_news_notification_default_value).toBoolean()

        when(prefs.getBoolean(key,defaultValue)){
            true -> enableNewsUpdateNotification()
            else -> disableNewsUpdateNotification()
        }
    }

    private fun handleNewsUpdateNotificationVibrationPrefChange() {
        enableNewsUpdateNotification()
    }

    private fun enableNewsUpdateNotification() {
        val prefs = preferenceScreen.sharedPreferences
        val vibrationPrefKey = getString(R.string.pref_news_notification_vibration_key)
        val vibrationDefaultValue = getString(R.string.pref_news_notification_vibration_default_value).toBoolean()
        val vibrationPref = prefs.getBoolean(vibrationPrefKey,vibrationDefaultValue)

        viewModel.configNewsUpdateNotification(
            NewsNotificationConfig(
                enabled = true,
                vibrate = vibrationPref
            )
        )
    }

    private fun disableNewsUpdateNotification() {
        viewModel.configNewsUpdateNotification(NewsNotificationConfig(enabled = false))
    }
}