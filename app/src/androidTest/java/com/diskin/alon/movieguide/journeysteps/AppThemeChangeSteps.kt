package com.diskin.alon.movieguide.journeysteps

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.R
import com.diskin.alon.movieguide.settings.presentation.R.string
import com.diskin.alon.movieguide.util.DeviceUtil
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers.equalTo

/**
 * Step definitions for 'User change app theme' scenario.
 */
class AppThemeChangeSteps : GreenCoffeeSteps() {

    private var expectedNightMode: Int = -1

    @Given("^User launched app from device home$")
    fun userLaunchedAppFromDeviceHome() {
        DeviceUtil.openDeviceHome()
        DeviceUtil.launchApp()
    }

    @And("^User open app settings screen$")
    fun openedAppSettingsScreen() {
        onView(withId(R.id.settings))
            .perform(click())
    }

    @When("^User select different app theme$")
    fun userSelectsDifferentAppTheme() {
        onView(withClassName(equalTo(RecyclerView::class.java.name)))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(string.pref_theme_title)),
                    click()
                )
            )

        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val themePrefKey = context.getString(R.string.pref_theme_key)
        val currentTheme = prefs.getString(themePrefKey,"")!!

        when(currentTheme) {
            context.getString(R.string.pref_theme_day_value) -> {
                onView(withText(R.string.pref_theme_night_entry))
                    .inRoot(RootMatchers.isDialog())
                    .perform(click())
                expectedNightMode = AppCompatDelegate.MODE_NIGHT_YES
            }

            context.getString(R.string.pref_theme_night_value) -> {
                onView(withText(R.string.pref_theme_day_entry))
                    .inRoot(RootMatchers.isDialog())
                    .perform(click())
                expectedNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }
        }
    }

    @Then("^App visual theme should be changed as selected$")
    fun appVisualThemeShouldBeChangedAsSelected() {
        assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(expectedNightMode)
    }
}