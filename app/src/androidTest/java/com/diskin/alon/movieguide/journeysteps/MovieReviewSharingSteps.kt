package com.diskin.alon.movieguide.journeysteps

import android.content.Intent
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
import com.google.common.truth.Truth
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject

/**
 * Step definitions for 'User share movie review' scenario.
 */
class MovieReviewSharingSteps(server: MockWebServer) : GreenCoffeeSteps() {

    private val dispatcher = TestDispatcher()

    init {
        // Prepare test server
        server.setDispatcher(dispatcher)
    }

    @Given("^User launched app from device home$")
    fun user_launched_app_from_device_home() {
        DeviceUtil.openDeviceHome()
        DeviceUtil.launchApp()
    }

    @And("^Open movie reviews screen$")
    fun open_movie_reviews_screen() {
        onView(withId(R.id.reviews))
            .perform(click())
    }

    @When("^User selects to read first listed review$")
    fun user_selects_to_read_first_listed_review() {
        onView(withId(R.id.movies))
            .perform(
                actionOnItemAtPosition<MovieViewHolder>(
                    0,
                    click()
                )
            )
    }

    @And("^User share review$")
    fun user_share_review() {
        Intents.init()

        onView(withId(R.id.action_share))
            .perform(click())
    }

    @Then("^App should share review url$")
    fun app_should_share_review_url() {
        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent

        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))
        Truth.assertThat(intent.type).isEqualTo("text/plain")
        Truth.assertThat(intent.getStringExtra(Intent.EXTRA_TEXT)).isEqualTo(expectedMovieReviewUrl())

        Intents.release()
        DeviceUtil.pressBack()
    }

    private fun expectedMovieReviewUrl(): String {
        val json = FileUtil.readStringFromFile(dispatcher.movieDetailResource)
        val jsonObject = JSONObject(json)

        return "https://www.themoviedb.org/movie/".plus(jsonObject.getString("id"))
    }

    private class TestDispatcher: Dispatcher() {
        val moviesResource = "assets/json/themoviedb_movies_sorted_by_popularity.json"
        val movieDetailResource = "assets/json/themoviedb_movie_detail.json"
        val movieTrailersResource = "assets/json/themoviedb_movie_trailers.json"

        private val moviesPath = "/discover/movie"
        private val movieDetailPath = "/movie/724989"
        private val movieTrailersPath = "/movie/724989/videos"

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.requestUrl.uri().path) {
                moviesPath -> MockResponse()
                    .setBody(FileUtil.readStringFromFile(moviesResource))
                    .setResponseCode(200)

                movieDetailPath -> MockResponse()
                    .setBody(FileUtil.readStringFromFile(movieDetailResource))
                    .setResponseCode(200)

                movieTrailersPath -> MockResponse()
                    .setBody(FileUtil.readStringFromFile(movieTrailersResource))
                    .setResponseCode(200)

                else -> MockResponse().setResponseCode(404)
            }
        }
    }
}