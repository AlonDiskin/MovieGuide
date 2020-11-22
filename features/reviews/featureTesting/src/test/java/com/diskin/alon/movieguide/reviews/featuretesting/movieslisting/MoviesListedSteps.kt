package com.diskin.alon.movieguide.reviews.featuretesting.movieslisting

import android.content.Context
import android.os.Looper
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.reviews.data.remote.*
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesAdapter
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.mockkObject
import io.mockk.verify
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.allOf
import org.robolectric.Shadows

/**
 * Step definitions for 'Reviewed movies listed by sorting' scenario.
 */
class MoviesListedSteps(server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var scenario: FragmentScenario<MoviesFragment>
    private val dispatcher = TestDispatcher()

    init {
        // Prepare mock web server for test scenario
        server.start()
        server.setDispatcher(dispatcher)

        // Mock out image loader
        mockkObject(ImageLoader)
    }

    @Given("^User opened movie reviews screen$")
    fun userOpenedMovieReviewsScreen() {
        scenario = FragmentScenario.launchInContainer(MoviesFragment::class.java)
    }

    @Then("^Reviewed movies should be listed and sorted by movie popularity in desc order$")
    fun reviewedMoviesShouldBeListedAndSortedByPopularityInDescOrder() {
        // Verify all movies from mock server are shown as expected
        val expectedUiMovies = expectedUiMovies(
            dispatcher.moviesByPopularityResourcePath
        )

        checkExpectedUiMoviesDataShown(expectedUiMovies)
    }

    @When("User select \"([^\"]*)\" sorting")
    fun userSelectSorting(sorting: String) {
        // Click on tested sorting
        val context = ApplicationProvider.getApplicationContext<Context>()
        val menItem = when(sorting) {
            "rating" -> ActionMenuItem(
                context,
                0,
                R.id.action_sort_rating,
                0,
                0,
                null
            )

            "release date" -> ActionMenuItem(
                context,
                0,
                R.id.action_sort_date,
                0,
                0,
                null
            )

            else -> throw IllegalArgumentException("unknown scenario argument:${sorting}")
        }

        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(menItem)
        }

        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("Reviewed movies should be listed and sorted by \"([^\"]*)\" in desc order")
    fun reviewedMoviesShouldBeListedAndSortedInDescOrder(sorting: String) {
        // Verify all movies from mock server are shown as expected
        when(sorting) {
            "rating" -> {
                val expectedUiMovies = expectedUiMovies(
                    dispatcher.moviesByRatingResourcePath
                )

                checkExpectedUiMoviesDataShown(expectedUiMovies)
            }

            "release date" -> {
                val expectedUiMovies = expectedUiMovies(
                    dispatcher.moviesByDateResourcePath
                )

                checkExpectedUiMoviesDataShown(expectedUiMovies)
            }

            else -> throw IllegalArgumentException("unknown scenario argument:${sorting}")
        }
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
            onView(withRecyclerView(R.id.movies).atPosition(index))
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

            onView(withRecyclerView(R.id.movies).atPosition(index))
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
        val moviesByPopularityResourcePath = "json/themoviedb_movies_sorted_by_popularity.json"
        val moviesByRatingResourcePath = "json/themoviedb_movies_sorted_by_rating.json"
        val moviesByDateResourcePath = "json/themoviedb_movies_sorted_by_date.json"

        override fun dispatch(request: RecordedRequest): MockResponse {
            val sortedByPopularityPath = "/$MOVIE_DB_MOVIES_PATH?$MOVIE_DB_POP_MOVIES_PARAMS&$MOVIE_DB_PARAM_PAGE=1"
            val sortedByDatePath = "/$MOVIE_DB_MOVIES_PATH?$MOVIE_DB_RELEASE_DATE_MOVIES_PARAMS&$MOVIE_DB_PARAM_PAGE=1"
            val sortedByRatingPath = "/$MOVIE_DB_MOVIES_PATH?$MOVIE_DB_RATING_MOVIES_PARAMS&$MOVIE_DB_PARAM_PAGE=1"

            return when(request.path) {
                sortedByPopularityPath -> MockResponse()
                    .setBody(getJsonFromResource(moviesByPopularityResourcePath))
                    .setResponseCode(200)

                sortedByDatePath -> MockResponse()
                    .setBody(getJsonFromResource(moviesByDateResourcePath))
                    .setResponseCode(200)

                sortedByRatingPath -> MockResponse()
                    .setBody(getJsonFromResource(moviesByRatingResourcePath))
                    .setResponseCode(200)

                else -> MockResponse().setResponseCode(404)
            }
        }
    }

}