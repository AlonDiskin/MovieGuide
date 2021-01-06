package com.diskin.alon.movieguide.reviews.featuretesting.reviewsharing

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.featuretesting.R
import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
import com.google.common.truth.Truth
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
 * Step definitions for 'Movie review is shared' scenario.
 */
class ReviewSharingSteps(server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<MovieReviewActivity>
    private val dispatcher = TestDispatcher()

    init {
        // Prepare test server
        server.setDispatcher(dispatcher)
    }

    @Given("^User read movie review$")
    fun userReadMovieReview() {
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val keyMovieId = context.getString(R.string.movie_id_arg)
        val movieIdArg = dispatcher.movieResourceId.toString()

        Intent(context, MovieReviewActivity::class.java)
            .apply { putExtra(keyMovieId, movieIdArg) }
            .also { scenario = ActivityScenario.launch(it) }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User Selects to share review data$")
    fun userSelectsToShareReviewData() {
        Intents.init()

        onView(withId(R.id.action_share))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Review web url should be shared$")
    fun reviewShouldBeShared(){
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent
        val context = ApplicationProvider.getApplicationContext<Context>()!!

        Truth.assertThat(intent.type)
            .isEqualTo(context.getString(com.diskin.alon.movieguide.reviews.presentation.R.string.mime_type_text))
        Truth.assertThat(intent.getStringExtra(Intent.EXTRA_TEXT))
            .isEqualTo(expectedSharedMovieWebUrl())

        Intents.release()
    }

    private fun expectedSharedMovieWebUrl(): String {
        val movieDetailJson = getJsonFromResource(dispatcher.movieDetailResourcePath)
        val jsonDetailObject = JSONObject(movieDetailJson)

        return "https://www.themoviedb.org/movie/".plus(jsonDetailObject.getString("id"))
    }

    private class TestDispatcher : Dispatcher() {
        val movieDetailResourcePath = "json/themoviedb_movie_detail.json"
        val movieTrailersResourcePath = "json/themoviedb_movie_trailers.json"
        val movieResourceId = 724989
        private val movieDetailPath = "/3/movie/$movieResourceId"
        private val movieTrailersPath = "/3/movie/$movieResourceId/videos"

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.requestUrl.uri().path) {
                movieDetailPath -> {
                    return if (isRequestHasApiKeyParam(request)) {
                        MockResponse()
                            .setBody(getJsonFromResource(movieDetailResourcePath))
                            .setResponseCode(200)
                    } else{
                        MockResponse().setResponseCode(404)
                    }
                }

                movieTrailersPath -> {
                    return if (isRequestHasApiKeyParam(request)) {
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

        private fun isRequestHasApiKeyParam(request: RecordedRequest): Boolean {
            return request.requestUrl.queryParameter("api_key") == BuildConfig.MOVIE_DB_API_KEY
        }
    }
}