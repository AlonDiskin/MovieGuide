package com.diskin.alon.movieguide.reviews.featuretesting.trailerviewing

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.featuretesting.R
import com.diskin.alon.movieguide.reviews.presentation.R.id
import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
import com.diskin.alon.movieguide.reviews.presentation.controller.TrailersAdapter.MovieTrailerViewHolder
import com.google.android.material.appbar.AppBarLayout
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject
import org.robolectric.Shadows

/**
 * Step definitions for 'User select movie trailer to view' scenario.
 */
class TrailerViewingSteps(server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<MovieReviewActivity>
    private val dispatcher = TestDispatcher()

    init {
        // Prepare test server
        server.setDispatcher(dispatcher)
    }

    @Given("^User read movie review that has movie trailers web links$")
    fun userReadMovieReviewThatHasMovieTrailerWebLinks() {
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val keyMovieId = context.getString(R.string.movie_id_arg)
        val movieIdArg = dispatcher.movieResourceId.toString()

        Intent(context, MovieReviewActivity::class.java)
            .apply { putExtra(keyMovieId, movieIdArg) }
            .also { scenario = ActivityScenario.launch(it) }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User selects to view the first trailer$")
    fun userSelectsToViewTheFirstTrailer() {
        Intents.init()

        scenario.onActivity { it.findViewById<AppBarLayout>(R.id.appBar).setExpanded(false) }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withId(id.trailers))
            .perform(
                actionOnItemAtPosition<MovieTrailerViewHolder>(
                    0,
                    click()
                )
            )
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^App should send user to view trailer via other device app$")
    fun appShouldSendUserToViewTrailerViaOtherDeviceApp() {
        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent

        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        assertThat(intent.action).isEqualTo(Intent.ACTION_VIEW)
        assertThat(intent.data).isEqualTo(Uri.parse(expectedTrailerUrl()))

        Intents.release()
    }

    private fun expectedTrailerUrl(): String {
        val movieTrailersJson = getJsonFromResource(dispatcher.movieTrailersResourcePath)
        val jsonTrailersObject = JSONObject(movieTrailersJson)
        val jsonTrailersArray = jsonTrailersObject.getJSONArray("results")

        return "https://www.youtube.com/watch?v=".plus(
            jsonTrailersArray.getJSONObject(0).getString("key")
        )
    }

    private class TestDispatcher : Dispatcher() {
        val movieDetailResourcePath = "json/themoviedb_movie_detail.json"
        val movieTrailersResourcePath = "json/themoviedb_movie_trailers.json"
        val movieResourceId = 724989

        override fun dispatch(request: RecordedRequest): MockResponse {
            val movieDetailPath = "/3/movie/$movieResourceId"
            val movieTrailersPath = "/3/movie/$movieResourceId/videos"

            return when(request.requestUrl.uri().path) {
                movieDetailPath -> {
                    return if (
                        request.requestUrl.queryParameter("api_key") ==
                        BuildConfig.MOVIE_DB_API_KEY
                    ) {
                        MockResponse()
                            .setBody(getJsonFromResource(movieDetailResourcePath))
                            .setResponseCode(200)
                    } else{
                        MockResponse().setResponseCode(404)
                    }
                }

                movieTrailersPath -> {
                    return if (
                        request.requestUrl.queryParameter("api_key") ==
                        BuildConfig.MOVIE_DB_API_KEY
                    ) {
                        MockResponse()
                            .setBody(getJsonFromResource(movieTrailersResourcePath))
                            .setResponseCode(200)
                    } else{
                        MockResponse().setResponseCode(404)
                    }
                }

                else -> MockResponse().setResponseCode(404)
            }
        }
    }
}