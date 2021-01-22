package com.diskin.alon.movieguide.userjourneytests

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.movieguide.R
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesAdapter.HeadlineViewHolder
import com.diskin.alon.movieguide.util.DeviceUtil
import com.diskin.alon.movieguide.util.FileUtil
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray
import java.net.URLEncoder

/**
 * Step definitions for 'User share article' scenario.
 */
class ArticleSharingSteps(server: MockWebServer) : GreenCoffeeSteps() {

    private val dispatcher = TestDispatcher()

    init {
        // Prepare test server
        server.setDispatcher(dispatcher)
    }

    @Given("^User launched app from device home$")
    fun userLaunchedAppFromDeviceHome() {
        DeviceUtil.openDeviceHome()
        DeviceUtil.launchApp()
    }

    @And("^Open news screen$")
    fun openedNewsScreen() {
        // Open news screen
        onView(withId(R.id.news))
            .perform(click())
    }

    @When("^User selects to read first listed article$")
    fun userSelectsToReadFirstListedArticle() {
        // Open first article
        onView(withId(R.id.headlines))
            .perform(actionOnItemAtPosition<HeadlineViewHolder>(0,click()))
    }

    @When("^User share article$")
    fun userShareArticle() {
        Intents.init()

        // Select to share article
        onView(withId(R.id.action_share))
            .perform(click())
    }

    @Then("^App should share article url$")
    fun appShouldShareArticleUrl() {
        // Verify app share article url via android sharing ui sheet
        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent

        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))
        assertThat(intent.type).isEqualTo("text/plain")
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT)).isEqualTo(expectedArticleUrl())

        Intents.release()
        DeviceUtil.pressBack()
    }

    private fun expectedArticleUrl(): String {
        val json = FileUtil.readStringFromFile(dispatcher.sharedArticleResource)
        val jsonArray = JSONArray(json)
        val jsonEntryObject = jsonArray.getJSONObject(0)!!

        return jsonEntryObject.getString("originId")
    }

    private class TestDispatcher: Dispatcher() {
        val articlesResource = "assets/json/feedly_movie_news_stream.json"
        val sharedArticleResource = "assets/json/feedly_movie_news_entry.json"
        private val feedPath = "/streams/contents"
        private val entryPath = "/entries/".plus(
            URLEncoder.encode(
                "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e6c31519:81d950:5d3e1c98",
                "UTF-8"
            )
        )

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.requestUrl.url().path) {
                feedPath -> MockResponse()
                    .setBody(FileUtil.readStringFromFile(articlesResource))
                    .setResponseCode(200)

                entryPath -> MockResponse()
                    .setBody(FileUtil.readStringFromFile(sharedArticleResource))
                    .setResponseCode(200)

                else -> MockResponse().setResponseCode(404)
            }
        }
    }
}