package com.diskin.alon.movieguide.journeysteps

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.diskin.alon.movieguide.R
import com.diskin.alon.movieguide.news.presentation.controller.NewsHeadlinesAdapter.NewsHeadlineViewHolder
import com.diskin.alon.movieguide.news.presentation.model.Article
import com.diskin.alon.movieguide.util.DeviceUtil
import com.diskin.alon.movieguide.util.FileReader
import com.diskin.alon.movieguide.util.MockWebServerDispatcher
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.joda.time.LocalDateTime
import org.json.JSONArray

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
        // Open news headline screen
        onView(withId(R.id.news))
            .perform(click())
    }

    @When("^User selects to read first headline article$")
    fun userSelectsToReadFirstHeadlineArticle() {
        // Open first headline article
        onView(withId(R.id.headlines))
            .perform(actionOnItemAtPosition<NewsHeadlineViewHolder>(0,click()))
    }

    @And("^Read article content$")
    fun readArticleContent() {
        // Verify expected article is shown
        val article = parseUiArticleFromTestWebResource()

        onView(withId(R.id.title))
            .check(matches(withText(article.title)))

        onView(withId(R.id.published))
            .check(matches(withText(article.date)))

        onView(withId(R.id.author))
            .check(matches(withText(article.author)))

        onView(withId(R.id.content))
            .check(matches(withText(article.content)))
    }

    @When("^User open article sharing menu$")
    fun userOpenArticleSharingMenu() {
        // Init intants matching n api
        Intents.init()

        // Select to share article
        onView(withId(R.id.action_share))
            .perform(click())
    }

    @Then("^Sharing menu should be displayed$")
    fun sharingMenuShouldBeDisplayed() {
        // Verify app opened android sharing ui sheet
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))

        // Close intants matching n api
        Intents.release()

        // Close device sharing ui
        DeviceUtil.pressBack()
    }

    private fun parseUiArticleFromTestWebResource(): Article {
        val json = FileReader.readStringFromFile(MockWebServerDispatcher.MOVIE_NEWS_ENTRY_RES)
        val jsonArray = JSONArray(json)
        val jsonEntryObject = jsonArray.getJSONObject(0)!!
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val articleDateFormat = context.getString(R.string.article_date_format)

        return Article(
            jsonEntryObject.getString("title"),
            jsonEntryObject.getString("author"),
            jsonEntryObject.getJSONObject("summary").getString("content"),
            LocalDateTime(jsonEntryObject.getLong("published")).toString(articleDateFormat),
            jsonEntryObject.getJSONObject("visual").getString("url"),
            jsonEntryObject.getString("originId")
        )
    }
}