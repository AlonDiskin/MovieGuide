package com.diskin.alon.movieguide.journeysteps

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.R
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.presentation.R.string
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesAdapter.MovieViewHolder
import com.diskin.alon.movieguide.runner.TestApp
import com.diskin.alon.movieguide.util.DataBindingIdlingResource
import com.diskin.alon.movieguide.util.DeviceUtil
import com.diskin.alon.movieguide.util.FileUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.json.JSONObject

/**
 * Step definitions for 'User search for movie' scenario.
 */
class MovieSearchSteps(private val server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var dispatcher: TestDispatcher
    private lateinit var idlingResource: DataBindingIdlingResource

    @Given("^User launched app from device home$")
    fun user_launched_app_from_device_home() {
        DeviceUtil.openDeviceHome()
        DeviceUtil.launchApp()

        val testApp = ApplicationProvider.getApplicationContext<Context>() as TestApp
        idlingResource = DataBindingIdlingResource(testApp.currentActivity)
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @And("^Open movies screen$")
    fun open_movies_screen() {
        onView(withId(R.id.reviews))
            .perform(click())
    }

    @And("^User perform a search with the query \"([^\"]*)\"$")
    fun user_perform_a_search_with_the_query_something(query: String) {
        dispatcher = TestDispatcher(query)
        server.setDispatcher(dispatcher)

        onView(withContentDescription(R.string.title_action_search))
            .perform(click())
        onView(withHint(string.search_hint))
            .perform(typeText(query))
            .perform(pressImeActionButton())
    }

    @And("^User read first resulted review detail$")
    fun user_read_first_resulted_review_detail() {
        onView(withId(R.id.search_results))
            .perform(
                actionOnItemAtPosition<MovieViewHolder>(
                    0,
                    click()
                )
            )
    }

    @Then("^Movie review detail should be shown$")
    fun movie_review_detail_should_be_shown() {
        val review = expectedUiMovieReview()

        onView(withId(R.id.title))
            .check(ViewAssertions.matches(withText(review.title)))

        onView(withId(R.id.rating))
            .check(ViewAssertions.matches(withText(review.rating)))

        onView(withId(R.id.genre))
            .check(ViewAssertions.matches(withText(review.genres)))

        onView(withId(R.id.release_date))
            .check(ViewAssertions.matches(withText(review.releaseDate)))

        onView(withId(R.id.summary))
            .check(ViewAssertions.matches(withText(review.summary)))

        onView(withId(R.id.review_text))
            .check(ViewAssertions.matches(withText(review.review)))
    }

    private fun expectedUiMovieReview(): UiMovieReviewData {
        val movieDetailJson = FileUtil.readStringFromFile(dispatcher.movieDetailResourcePath)
        val movieTrailersJson = FileUtil.readStringFromFile(dispatcher.movieTrailersResourcePath)
        val jsonDetailObject = JSONObject(movieDetailJson)
        val jsonTrailersObject = JSONObject(movieTrailersJson)
        val jsonGenresArray = jsonDetailObject.getJSONArray("genres")
        val jsonTrailersArray = jsonTrailersObject.getJSONArray("results")
        val releaseDate = LocalDate.parse(jsonDetailObject.getString("release_date")).toDate().time
        var genres = ""
        val trailersUrls = mutableListOf<String>()

        for (i in 0 until jsonGenresArray.length()) {
            val genreJsonObject = jsonGenresArray.getJSONObject(i)
            val genre = genreJsonObject.getString("name")
            genres = if (i == jsonGenresArray.length() - 1) {
                genres.plus(genre)
            } else {
                genres.plus(genre).plus(",")
            }
        }

        for (i in 0 until jsonTrailersArray.length()) {
            val key = jsonTrailersArray.getJSONObject(i).getString("key")
            val url = "https://img.youtube.com/vi/".plus(key).plus("/0.jpg")

            trailersUrls.add(url)
        }

        return UiMovieReviewData(
            jsonDetailObject.getString("title"),
            jsonDetailObject.getDouble("vote_average").toString(),
            genres,
            LocalDateTime(releaseDate).toString("dd MMM yyyy"),
            jsonDetailObject.getString("overview"),
            "review_stub",
            "http://image.tmdb.org/t/p/w342".plus(jsonDetailObject.getString("backdrop_path")),
            trailersUrls
        )
    }

    private class TestDispatcher(val query: String) : Dispatcher() {
        val searchResultsRes = "assets/json/themoviedb_movies_sorted_by_popularity.json"
        val movieDetailResourcePath = "assets/json/themoviedb_movie_detail.json"
        val movieTrailersResourcePath = "assets/json/themoviedb_movie_trailers.json"
        private val moviesSearchPath = "/search/movie"
        private val movieDetailPath = "/movie/724989"
        private val movieTrailersPath = "/movie/724989/videos"

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
                        MockResponse()
                            .setBody(FileUtil.readStringFromFile(searchResultsRes))
                            .setResponseCode(200)

                    } else {
                        MockResponse().setResponseCode(404)
                    }
                }

                movieDetailPath -> {
                    return if (
                        request.requestUrl.queryParameter("api_key") ==
                        BuildConfig.MOVIE_DB_API_KEY
                    ) {
                        MockResponse()
                            .setBody(FileUtil.readStringFromFile(movieDetailResourcePath))
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
                            .setBody(FileUtil.readStringFromFile(movieTrailersResourcePath))
                            .setResponseCode(200)
                    } else{
                        MockResponse().setResponseCode(404)
                    }
                }

                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    private data class UiMovieReviewData(val title: String,
                                 val rating: String,
                                 val genres: String,
                                 val releaseDate: String,
                                 val summary: String,
                                 val review: String,
                                 val backDropImageUrl: String,
                                 val trailersThumbnailUrls: List<String>)
}