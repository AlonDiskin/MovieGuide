package com.diskin.alon.movieguide.journeysteps

import android.app.ActivityManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.movieguide.util.DeviceUtil
import com.diskin.alon.movieguide.R
import com.diskin.alon.movieguide.home.presentation.MainActivity
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When

/**
 * Step definitions for app navigation journey scenario.
 */
class AppNavSteps : GreenCoffeeSteps() {

    @Given("^User launched app from device home$")
    fun userLaunchedAppFromDeviceHome() {
        DeviceUtil.openDeviceHome()
        DeviceUtil.launchApp()
    }

    @Then("^Movie news should be shown in home screen$")
    fun movieNewsShouldBeShownInHomeScreen() {
        // Verify main activity is shown
        val am = ApplicationProvider.getApplicationContext<Context>()
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(1)
        val foregroundActivityName =  tasks.first().topActivity!!.className

        assertThat(foregroundActivityName).isEqualTo(MainActivity::class.java.name)

        // Verify news feature is shown
        onView(withId(R.id.news_root))
            .check(matches(isDisplayed()))
    }

    @When("^User navigates to \"([^\"]*)\"$")
    fun userNavigatesTo(feature: String) {
        when(feature) {
            "reviews" -> {
                onView(withId(R.id.reviews))
                    .perform(click())
            }

            "settings" -> {
                onView(withId(R.id.settings))
                    .perform(click())
            }
        }
    }

    @Then("^\"([^\"]*)\" ui should be shown$")
    fun uiShouldBeShown(feature: String) {
        when(feature) {
            "reviews" -> {
                onView(withId(R.id.movies_root))
                    .check(matches(isDisplayed()))
            }

            "settings" -> {
                onView(withId(R.id.settings_root))
                    .check(matches(isDisplayed()))
            }
        }
    }
}