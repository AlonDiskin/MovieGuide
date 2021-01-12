package com.diskin.alon.movieguide.reviews.featuretesting.favoritingmovie

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.featuretesting.R
import com.diskin.alon.movieguide.reviews.featuretesting.TestDatabase
import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
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
import org.robolectric.Shadows

/**
 * Step definitions for 'User add movie to favorites' scenario.
 */
class FavoritingMovieSteps(
    server: MockWebServer,
    private val db: TestDatabase
) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<MovieReviewActivity>
    private val dispatcher = TestDispatcher()

    init {
        // Prepare test server
        server.setDispatcher(dispatcher)
    }

    @Given("^User has no favorite movies$")
    fun user_has_no_favorite_movies() {
        val queryResult = db.compileStatement("SELECT COUNT(*) FROM favorites").simpleQueryForLong()
        assertThat(queryResult).isEqualTo(0)
    }

    @And("^User open movie detail screen$")
    fun user_open_movie_detail_screen() {
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val keyMovieId = context.getString(R.string.movie_id_arg)
        val movieIdArg = dispatcher.movieResourceId.toString()

        Intent(context, MovieReviewActivity::class.java)
            .apply { putExtra(keyMovieId, movieIdArg) }
            .also { scenario = ActivityScenario.launch(it) }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User select to favorite movie$")
    fun user_select_to_favorite_movie() {
        onView(withId(R.id.action_favoriting))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Movie should be shown as favorite$")
    fun movie_should_be_shown_as_favorite() {
        onView(withId(R.id.action_favoriting))
            .check(
                matches(
                    withContentDescription(R.string.title_action_unfavorite_movie)
                )
            )
    }

    @And("^Movie should be added to user favorite movies$")
    fun movie_should_be_added_to_user_favorite_movies() {
        val queryResult = db.compileStatement("SELECT COUNT(*) FROM favorites").simpleQueryForLong()
        assertThat(queryResult).isEqualTo(1)
    }

    @When("^User select to un favorite movie$")
    fun user_select_to_un_favorite_movie() {
        onView(withId(R.id.action_favoriting))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Movie should be shown as not favorite$")
    fun movie_should_be_shown_as_not_favorite() {
        onView(withId(R.id.action_favoriting))
            .check(
                matches(
                    withContentDescription(R.string.title_action_favorite_movie)
                )
            )
    }

    @And("^Movie should be removed from user favorite movies$")
    fun movie_should_be_removed_from_user_favorite_movies() {
        val queryResult = db.compileStatement("SELECT COUNT(*) FROM favorites").simpleQueryForLong()
        assertThat(queryResult).isEqualTo(0)
    }

    private class TestDispatcher: Dispatcher() {
        val movieResourceId = 393209
        private val movieDetailResource = "json/themoviedb_movie_detail.json"
        private val movieTrailersResource = "json/themoviedb_movie_trailers.json"
        private val movieDetailPath = "/movie/$movieResourceId"
        private val movieTrailersPath = "/movie/$movieResourceId/videos"

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.requestUrl.uri().path) {
                movieDetailPath -> {
                    return if (isRequestHasApiKeyParam(request)) {
                        MockResponse()
                            .setBody(getJsonFromResource(movieDetailResource))
                            .setResponseCode(200)
                    } else{
                        MockResponse().setResponseCode(404)
                    }
                }

                movieTrailersPath -> {
                    return if (isRequestHasApiKeyParam(request)) {
                        MockResponse()
                            .setBody(getJsonFromResource(movieTrailersResource))
                            .setResponseCode(200)
                    } else{
                        MockResponse().setResponseCode(404)
                    }
                }

                else -> MockResponse().setResponseCode(404)
            }
        }

        private fun isRequestHasApiKeyParam(request: RecordedRequest): Boolean {
            return request.requestUrl.queryParameter("api_key") == BuildConfig.MOVIE_DB_API_KEY
        }
    }
}