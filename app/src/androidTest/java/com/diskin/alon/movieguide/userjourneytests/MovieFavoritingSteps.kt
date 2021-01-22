package com.diskin.alon.movieguide.userjourneytests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.diskin.alon.movieguide.R
import com.diskin.alon.movieguide.common.uitesting.isRecyclerViewItemsCount
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesAdapter
import com.diskin.alon.movieguide.util.DeviceUtil
import com.diskin.alon.movieguide.util.FileUtil
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
 * Step definitions for 'User favorite a movie' scenario.
 */
class MovieFavoritingSteps(server: MockWebServer) : GreenCoffeeSteps() {

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

    @And("^Open movies screen$")
    fun open_movies_screen() {
        onView(withId(R.id.reviews))
            .perform(click())
    }

    @And("^User rotate device to land$")
    fun user_rotate_device_to_land() {
        DeviceUtil.rotateDeviceLand()
    }

    @When("^User favorite first listed movie$")
    fun user_favorite_first_listed_movie() {
        onView(withId(R.id.movies))
            .perform(
                actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                    0,
                    click()
                )
            )
        onView(withId(R.id.action_favoriting))
            .perform(click())
    }

    @And("^User rotate device to port$")
    fun user_rotate_device_to_port() {
        DeviceUtil.rotateDevicePort()
    }

    @And("^Select to see all favorite movies$")
    fun select_to_see_all_favorite_movies() {
        pressBack()
        onView(withId(R.id.action_sort))
            .perform(click())
        onView(withText(R.string.title_action_sort_favorite))
            .perform(click())
    }

    @Then("^Previously favorite movie should be shown$")
    fun previously_favorite_movie_should_be_shown() {
        val movie = expectedUiFavoriteMovie()

        onView(withId(R.id.movies))
            .check(matches(isRecyclerViewItemsCount(1)))
        onView(withId(R.id.title))
            .check(matches(withText(movie.title)))
        onView(withId(R.id.rating))
            .check(matches(withText(movie.rating)))
    }

    private fun expectedUiFavoriteMovie(): UiFavoriteMovie {
        val json = FileUtil.readStringFromFile(dispatcher.movieDetailResource)
        val jsonObject = JSONObject(json)

        return UiFavoriteMovie(
            jsonObject.getString("title"),
            jsonObject.getDouble("vote_average").toString()
        )
    }

    private class TestDispatcher: Dispatcher() {
        val movieDetailResource = "assets/json/themoviedb_movie_detail.json"
        private val moviesResource = "assets/json/themoviedb_movies_sorted_by_popularity.json"
        private val movieTrailersResource = "assets/json/themoviedb_movie_trailers.json"
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

    private data class UiFavoriteMovie(val title: String,val rating: String)
}