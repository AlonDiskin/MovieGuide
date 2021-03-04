package com.diskin.alon.movieguide.settings.featuretesting

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider

fun clearSharedPrefs() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    editor.clear()
    editor.commit()
}

fun getSharedPrefs(): SharedPreferences {
    val context = ApplicationProvider.getApplicationContext<Context>()
    return PreferenceManager.getDefaultSharedPreferences(context)
}