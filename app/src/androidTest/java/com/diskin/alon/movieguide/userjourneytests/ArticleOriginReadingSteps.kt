package com.diskin.alon.movieguide.userjourneytests

import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.movieguide.R
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesAdapter.MovieViewHolder
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
 * Step definitions for 'User open article origin' scenario.
 */
class ArticleOriginReadingSteps(server: MockWebServer) : GreenCoffeeSteps() {

    private val dispatcher = TestDispatcher()

    init {
        server.setDispatcher(dispatcher)
    }

    @Given("^User launched app from device home$")
    fun user_launched_app_from_device_home() {
        DeviceUtil.openDeviceHome()
        DeviceUtil.launchApp()
    }

    @And("^Open news screen$")
    fun open_news_screen() {
        onView(withId(R.id.news))
            .perform(click())
    }

    @When("^User selects to read first listed article$")
    fun user_selects_to_read_first_listed_article() {
        onView(withId(R.id.headlines))
            .perform(
                actionOnItemAtPosition<MovieViewHolder>(
                    0,
                    click()
                )
            )
    }

    @And("^Select to read original published article$")
    fun select_to_read_original_published_article() {
        Intents.init()
        onView(withId(R.id.fab))
            .perform(click())
    }

    @Then("^App should open article web url$")
    fun app_should_open_article_web_url() {
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))

        val articleUrl = expectedArticleUrl()
        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent

        assertThat(intent.action).isEqualTo(Intent.ACTION_VIEW)
        assertThat(intent.data).isEqualTo(Uri.parse(articleUrl))

        Intents.release()
        DeviceUtil.pressBack()
    }

    private fun expectedArticleUrl(): String {
        val json = FileUtil.readStringFromFile(dispatcher.articleResource)
        val jsonArray = JSONArray(json)
        val jsonObject = jsonArray.getJSONObject(0)

        return jsonObject.getString("originId")
    }

    private class TestDispatcher : Dispatcher() {
        val articleResource = "assets/json/feedly_movie_news_entry.json"
        private val entriesResource = "assets/json/feedly_movie_news_stream.json"
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
                    .setBody(FileUtil.readStringFromFile(entriesResource))
                    .setResponseCode(200)

                entryPath -> MockResponse()
                    .setBody(FileUtil.readStringFromFile(articleResource))
                    .setResponseCode(200)

                else -> MockResponse().setResponseCode(404)
            }
        }
    }
}