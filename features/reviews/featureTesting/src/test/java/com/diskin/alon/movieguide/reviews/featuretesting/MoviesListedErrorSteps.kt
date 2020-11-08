package com.diskin.alon.movieguide.reviews.featuretesting

import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.*
import com.diskin.alon.movieguide.reviews.data.*
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesAdapter
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler.Companion.ERR_API_SERVER
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler.Companion.ERR_DEVICE_NETWORK
import com.google.common.truth.Truth.*
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.mockkObject
import io.mockk.verify
import okhttp3.mockwebserver.*
import org.hamcrest.CoreMatchers.*
import org.robolectric.Shadows

/**
 * Step definitions for 'Reviews listing browsing errors handling' scenario.
 */
class MoviesListedErrorSteps(server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var scenario: FragmentScenario<MoviesFragment>
    private lateinit var expectedUiErrorMessage: String
    private val dispatcher = TestDispatcher()

    init {
        // Prepare test server
        server.start()
        server.setDispatcher(dispatcher)

        // Mock out image loader
        mockkObject(ImageLoader)
    }

    @Given("^Existing app error due to \"([^\"]*)\"$")
    fun existingAppErrorDueToCause(error: String) {
        // Prepare test server dispatcher, and expected error ui message according to error case
        when(error) {
            "device networking" ->{
                expectedUiErrorMessage = ERR_DEVICE_NETWORK
                dispatcher.mockDeviceNetworkingFail = true
            }

            "remote server" ->{
                expectedUiErrorMessage = ERR_API_SERVER
                dispatcher.mockRemoteServerFail = true
            }

            else -> throw IllegalArgumentException("unknown scenario argument:${error}")
        }
    }

    @When("^User open reviews screen$")
    fun userOpenReviewsScreen() {
        // Launch movies fragment
        scenario = FragmentScenario.launchInContainer(
            MoviesFragment::class.java,
            null,
            R.style.AppTheme,
            null
        )
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^User should be notifies with message describing error$")
    fun userShouldBeNotifiedWithMessageDescribingError() {
        // Verify a snackbar is showing expected error message
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
        // Verify a snackbar is providing a 'retry' option
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
        // Resolve error case
        when(error) {
            // Set test server dispatcher to errorless working state
            "device networking",
            "remote server" -> dispatcher.setDispatcherToWorkingState()

            else -> throw IllegalArgumentException("unknown scenario argument:${error}")
        }
    }

    @And("^User select to retry$")
    fun userSelectToRetry() {
        // Click on snackbar 'retry' button
        onView(withId(R.id.snackbar_action))
            .perform(ViewActions.click())

        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Reviews should be shown in reviews screen$")
    fun reviewsShouldBeShownInReviewsScreen() {
        // Verify expected movies are displayed
        val expectedUiMovies = getExpectedUiMoviesFromTestWebResource(
            dispatcher.moviesResourcePath
        )

        checkExpectedUiMoviesDataShown(expectedUiMovies)
    }

    private fun checkExpectedUiMoviesDataShown(movies: List<UiMovieData>) {
        // Check shown movies count is correct
        scenario.onFragment { fragment ->
            val itemsCount =
                fragment.view!!.findViewById<RecyclerView>(R.id.movies).adapter!!.itemCount
            assertThat(itemsCount).isEqualTo(movies.size)
        }

        movies.forEachIndexed { index, data ->
            // Scroll to expected movie data layout position
            onView(withId(R.id.movies))
                .perform(scrollToPosition<MoviesAdapter.MovieViewHolder>(index))

            Shadows.shadowOf(Looper.getMainLooper()).idle()

            // Check expected data is shown
            onView(
                withRecyclerView(R.id.movies)
                    .atPosition(index))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.title),
                                withText(data.title)
                            )
                        )
                    )
                )

            onView(
                withRecyclerView(R.id.movies)
                    .atPosition(index))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.rating),
                                withText(data.rating)
                            )
                        )
                    )
                )

            // Verify movie poster was loaded
            verify { ImageLoader.loadIntoImageView(any(),data.posterPath) }
        }
    }

    private class TestDispatcher : Dispatcher() {
        val moviesResourcePath = "json/themoviedb_movies_sorted_by_popularity.json"
        var mockDeviceNetworkingFail = false
        var mockRemoteServerFail = false

        override fun dispatch(request: RecordedRequest): MockResponse {
            val supportedPath = "/${MOVIE_DB_MOVIES_PATH}?${MOVIE_DB_POP_MOVIES_PARAMS}&${MOVIE_DB_PARAM_PAGE}=1"
            println(supportedPath)
            println(request.path)
            return if (mockDeviceNetworkingFail) {
                MockResponse()
                    .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
            } else {
                when (request.path) {
                    supportedPath -> {
                        if (mockRemoteServerFail) {
                            MockResponse().setResponseCode(500)
                        } else {
                            MockResponse()
                                .setBody(getJsonFromResource(moviesResourcePath))
                                .setResponseCode(200)
                        }
                    }

                    else -> throw IllegalArgumentException("unexpected mock server request")
                }
            }
        }

        fun setDispatcherToWorkingState() {
            mockRemoteServerFail = false
            mockDeviceNetworkingFail = false
        }
    }
}