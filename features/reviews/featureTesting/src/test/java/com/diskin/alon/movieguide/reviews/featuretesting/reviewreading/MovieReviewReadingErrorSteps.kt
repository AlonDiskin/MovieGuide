package com.diskin.alon.movieguide.reviews.featuretesting.reviewreading

import android.content.Context
import android.os.Bundle
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.featuretesting.R
import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewFragment
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.mockkObject
import io.mockk.verify
import okhttp3.mockwebserver.*
import org.hamcrest.CoreMatchers.allOf
import org.robolectric.Shadows

/**
 * Step definitions for 'Review reading error handling' scenario.
 */
class MovieReviewReadingErrorSteps(server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private lateinit var expectedUiErrorMessage: String
    private val dispatcher = TestDispatcher()

    init {
        // Prepare test server
        server.setDispatcher(dispatcher)

        // Mock out image loader
        mockkObject(ImageLoader)
    }

    @Given("^Existing app error due to \"([^\"]*)\"$")
    fun existingAppErrorDueToCause(error: String) {
        when(error) {
            "device networking" ->{
                expectedUiErrorMessage = "Network error,check device connectivity"
                dispatcher.setNetworkFailState()
            }

            "remote server" ->{
                expectedUiErrorMessage = "Server currently unavailable"
                dispatcher.setServerFailState()
            }

            else -> throw IllegalArgumentException("unknown scenario argument:${error}")
        }
        }


    @When("^User open movie review screen$")
    fun userOpenMovieReviewScreen() {
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val keyMovieId = context.getString(R.string.movie_id_arg)
        val movieIdArg = dispatcher.movieResourceId.toString()
        val bundle = Bundle().apply { putString(keyMovieId,movieIdArg) }
        scenario = launchFragmentInHiltContainer<MovieReviewFragment>(fragmentArgs = bundle)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^User should be notifies with message describing error$")
    fun userShouldBeNotifiedWithMessageDescribingError() {
        onView(withId(R.id.snackbar_text))
            .check(
                matches(
                    allOf(
                        withText(expectedUiErrorMessage),
                        isDisplayed()
                    )
                )
            )
    }

    @And("^Provided with retry option$")
    fun providedWithRetryOption() {
        onView(withId(R.id.snackbar_action))
            .check(
                matches(
                    allOf(
                        withText(R.string.action_retry),
                        isDisplayed()
                    )
                )
            )
    }

    @When("^Error \"([^\"]*)\" is resolved$")
    fun errorIsResolved(error: String) {
        when(error) {
            // Set test server dispatcher to errorless working state
            "device networking",
            "remote server" -> dispatcher.cancelErrorState()

            else -> throw IllegalArgumentException("unknown scenario argument:${error}")
        }
    }

    @And("^User select to retry$")
    fun userSelectToRetry() {
        onView(withId(R.id.snackbar_action))
            .perform(click())

        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Movie Review detail should be shown$")
    fun movieReviewDetailShouldBeShown() {
        val review = expectedUiMovieReview(
            dispatcher.movieDetailResourcePath,
            dispatcher.movieTrailersResourcePath
        )

        onView(withId(R.id.title))
            .check(matches(withText(review.title)))

        onView(withId(R.id.rating))
            .check(matches(withText(review.rating)))

        onView(withId(R.id.genre))
            .check(matches(withText(review.genres)))

        onView(withId(R.id.release_date))
            .check(matches(withText(review.releaseDate)))

        onView(withId(R.id.summary))
            .check(matches(withText(review.summary)))

        verify { ImageLoader.loadIntoImageView(any(),review.backDropImageUrl) }

        review.trailersThumbnailUrls.forEach { url ->
            verify { ImageLoader.loadIntoImageView(any(),url) }
        }
    }

    private class TestDispatcher : Dispatcher() {
        val movieDetailResourcePath = "json/themoviedb_movie_detail.json"
        val movieTrailersResourcePath = "json/themoviedb_movie_trailers.json"
        val movieResourceId = 724989
        private var networkingFail = false
        private var serverFail = false

        override fun dispatch(request: RecordedRequest): MockResponse {
            val movieDetailPath = "/movie/$movieResourceId"
            val movieTrailersPath = "/movie/$movieResourceId/videos"

            return when {
                networkingFail -> MockResponse()
                    .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)

                serverFail -> MockResponse().setResponseCode(500)

                else -> {
                    when (request.requestUrl.uri().path) {
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

                        else -> throw IllegalArgumentException("unexpected mock server request")
                    }
                }
            }
        }

        fun setNetworkFailState() {
            networkingFail = true
        }

        fun setServerFailState() {
            serverFail = true
        }

        fun cancelErrorState() {
            serverFail = false
            networkingFail = false
        }
    }
}