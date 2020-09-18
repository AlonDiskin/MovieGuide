package com.diskin.alon.movieguide.news

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.diskin.alon.movieguide.R
import com.diskin.alon.movieguide.util.DeviceUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When

/**
 * Step definitions for 'User share article' scenario.
 */
class UserShareArticleSteps : GreenCoffeeSteps() {

    @Given("^User launched app from device home$")
    fun userLaunchedAppFromDeviceHome() {
        DeviceUtil.openDeviceHome()
        DeviceUtil.launchApp()
    }

    @And("^Opened movie news screen$")
    fun openedMovieNewsScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.news))
            .perform(ViewActions.click())
    }

    @And("^Read first shown headline$")
    fun readFirstShownHeadline() {
        // Verify first shown headline display expected data
        TODO()
    }

    @When("^User selects to read first headline article$")
    fun userSelectsToReadFirstHeadlineArticle() {
        // Open first headline article

        // Verify article display expected data
        TODO()
    }

    @And("^Open article sharing menu$")
    fun openArticleSharingMenu() {
        // Select to share article
        TODO()
    }

    @Then("^Sharing menu should be displayed$")
    fun sharingMenuShouldBeDisplayed() {
        TODO()
    }
}