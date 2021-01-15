package com.diskin.alon.movieguide.reviews.featuretesting.reviewsearching

import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.common.uitesting.isRecyclerViewItemsCount
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.data.remote.MOVIE_DB_BASE_POSTER_PATH
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesAdapter
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesSearchFragment
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
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
import org.json.JSONObject
import org.robolectric.Shadows

/**
 * Step definitions for 'User search for movies' scenario.
 */
class SearchSteps(private val server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private lateinit var dispatcher: TestDispatcher

    init {
        // Mock out image loader
        mockkObject(ImageLoader)
    }

    @Given("^Reviewed movies containing \"([^\"]*)\" in title are \"([^\"]*)\"$")
    fun reviewed_movies_containing_query_in_title(query: String, existing: Boolean) {
        dispatcher = TestDispatcher(query, existing)
        server.setDispatcher(dispatcher)
    }

    @When("^User open movies search screen$")
    fun user_open_movies_search_screen() {
        scenario = launchFragmentInHiltContainer<MoviesSearchFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @And("^Perform search for reviewed movies with \"([^\"]*)\" query$")
    fun perform_search_for_reviewed_movies_with_query(query: String) {
        onView(withHint(R.string.search_hint))
            .perform(typeText(query))
            .perform(pressImeActionButton())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Search results should be shown for \"([^\"]*)\" movies$")
    fun search_results_should_be_shown_for_movies(existing: Boolean) {
        when(existing) {
            true -> {
                val movies = expectedUiMovies()

                onView(withId(R.id.search_results))
                    .check(matches(isRecyclerViewItemsCount(movies.size)))
                movies.forEachIndexed { index, movie ->
                    // Scroll to expected movie layout position
                    onView(withId(R.id.search_results))
                        .perform(
                            scrollToPosition<MoviesAdapter.MovieViewHolder>(
                                index
                            )
                        )

                    Shadows.shadowOf(Looper.getMainLooper()).idle()

                    // Check list item at scrolled position display expected data
                    onView(withRecyclerView(R.id.search_results).atPosition(index))
                        .check(
                            matches(
                                hasDescendant(
                                    allOf(
                                        withId(R.id.title),
                                        withText(movie.title)
                                    )
                                )
                            )
                        )

                    onView(withRecyclerView(R.id.search_results).atPosition(index))
                        .check(
                            matches(
                                hasDescendant(
                                    allOf(
                                        withId(R.id.rating),
                                        withText(movie.rating)
                                    )
                                )
                            )
                        )

                    verify { ImageLoader.loadIntoImageView(any(),movie.posterPath) }
                }
            }
            else -> {
                onView(withId(R.id.search_results))
                    .check(matches(isRecyclerViewItemsCount(0)))
            }
        }
    }

    private fun expectedUiMovies(): List<UiMovieData> {
        val json = getJsonFromResource(dispatcher.searchResultsRes)
        val jsonObject = JSONObject(json)
        val jsonArray = jsonObject.getJSONArray("results")
        val res = mutableListOf<UiMovieData>()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)

            res.add(
                UiMovieData(
                    item.getString("title"),
                    item.getDouble("vote_average").toString(),
                    MOVIE_DB_BASE_POSTER_PATH.plus(item.get("poster_path"))
                )
            )
        }

        return res
    }

    private data class UiMovieData(val title: String, val rating: String, val posterPath: String)

    private class TestDispatcher(val query: String,val existing: Boolean) : Dispatcher() {
        val searchResultsRes = "json/themoviedb_movies_sorted_by_popularity.json"
        private val emptySearchResults  = "json/themoviedb_empty_movies_result.json"
        private val moviesSearchPath = "/search/movie"

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.requestUrl.uri().path) {
                moviesSearchPath -> {
                    return if (
                        request.requestUrl.queryParameter("page") == "1" &&
                        request.requestUrl.queryParameter("include_adult") == "false" &&
                        request.requestUrl.queryParameter("language") == "en-US" &&
                        request.requestUrl.queryParameter("query") == query &&
                        request.requestUrl.queryParameter("api_key") == BuildConfig.MOVIE_DB_API_KEY
                    ) {
                        if (existing) {
                            MockResponse()
                                .setBody(getJsonFromResource(searchResultsRes))
                                .setResponseCode(200)
                        } else{
                            MockResponse()
                                .setBody(getJsonFromResource(emptySearchResults))
                                .setResponseCode(200)
                        }

                    } else {
                        MockResponse().setResponseCode(404)
                    }
                }

                else -> MockResponse().setResponseCode(404)
            }
        }

    }
}