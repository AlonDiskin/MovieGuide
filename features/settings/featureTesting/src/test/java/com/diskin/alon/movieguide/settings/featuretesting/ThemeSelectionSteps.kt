package com.diskin.alon.movieguide.settings.featuretesting

import android.content.Context
import android.content.SharedPreferences
import android.os.Looper
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.testing.FragmentScenario
import androidx.preference.ListPreference
import androidx.preference.get
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.settings.presentation.R
import com.diskin.alon.movieguide.settings.presentation.SettingsFragment
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.hamcrest.CoreMatchers.equalTo
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowAlertDialog

/**
 * Step definitions for 'User change app theme' scenario.
 */
class ThemeSelectionSteps : GreenCoffeeSteps() {

    private lateinit var scenario: FragmentScenario<SettingsFragment>

    @Given("^App theme has not been changed by user$")
    fun appThemeHasNotBeenChangedByUser() {
        clearSharedPrefs()
    }

    @And("^User open settings screen$")
    fun userOpenSettingsScreen() {
        scenario = FragmentScenario.launchInContainer(SettingsFragment::class.java,
            null, R.style.Theme_AppCompat_Light_DarkActionBar,null)
    }

    @Then("^App theme should be set to the default$")
    fun appThemeShouldBeSetAsDefault() {
        scenario.onFragment { fragment ->
            val key = fragment.getString(R.string.pref_theme_key)
            val themePref = fragment.preferenceScreen.get<ListPreference>(key)!!
            val defaultThemeEntry = fragment.getString(R.string.pref_theme_day_entry)

            // Verify default theme preference is shown in settings fragment ui
            assertThat(themePref.isShown)
            assertThat(themePref.summary).isEqualTo(defaultThemeEntry)

            // Verify theme preference set to 'day' theme
            val dayThemeValue = fragment.getString(R.string.pref_theme_day_value)
            val actualTheme = fragment.preferenceManager.sharedPreferences.getString(key,"")!!

            assertThat(actualTheme).isEqualTo(dayThemeValue)
        }
    }

    @When("^User select different theme$")
    fun userSelectsDifferentTheme() {
        onView(withClassName(equalTo(RecyclerView::class.java.name)))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.pref_theme_title)),
                    click()
                )
            )

        val dialog =
            ShadowAlertDialog.getLatestDialog() as AlertDialog
        val context = getApplicationContext<Context>()
        val nightThemeValue = context.getString(R.string.pref_theme_night_entry)
        val nightThemeDialogIndex = context.resources
            .getStringArray(R.array.pref_theme_entries).toList().indexOf(nightThemeValue)

        dialog.listView.performItemClick(
            dialog.listView.adapter.getView(nightThemeDialogIndex, null, null),
            nightThemeDialogIndex,
            dialog.listView.adapter.getItemId(nightThemeDialogIndex))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^App theme should change as selected$")
    fun appThemeShouldChangeAsSelected() {
        scenario.onFragment { fragment ->
            val key = fragment.getString(R.string.pref_theme_key)
            val themePref = fragment.preferenceScreen.get<ListPreference>(key)!!
            val nightThemeEntry = fragment.getString(R.string.pref_theme_night_entry)

            // Verify settings fragment show current theme preference as 'night'
            assertThat(themePref.isShown)
            assertThat(themePref.summary).isEqualTo(nightThemeEntry)

            // Verify theme preference changed
            val nightThemeValue = fragment.getString(R.string.pref_theme_night_value)
            val actualTheme = fragment.preferenceManager.sharedPreferences.getString(key,"")!!

            assertThat(actualTheme).isEqualTo(nightThemeValue)

            // Verify night mode is set
            assertThat(AppCompatDelegate.getDefaultNightMode())
                .isEqualTo(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun clearSharedPrefs() {
        val context = getApplicationContext<Context>()
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.clear()
        editor.commit()
    }
}