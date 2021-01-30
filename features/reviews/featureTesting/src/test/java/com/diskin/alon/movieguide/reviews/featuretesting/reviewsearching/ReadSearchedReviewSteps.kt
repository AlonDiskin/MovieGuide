package com.diskin.alon.movieguide.reviews.featuretesting.reviewsearching

import android.content.Context
import android.os.Bundle
import android.os.Looper
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.featuretesting.R
import com.diskin.alon.movieguide.reviews.presentation.R.string
import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewFragment
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
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.json.JSONObject
import org.robolectric.Shadows

/**
 * Step definitions for 'User read search result review' scenario.
 */
class ReadSearchedReviewSteps(private val server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var searchScenario: ActivityScenario<HiltTestActivity>
    private lateinit var reviewScenario: ActivityScenario<HiltTestActivity>
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    private lateinit var dispatcher: TestDispatcher

    init {
        // Setup nav controller
        navController.setGraph(R.navigation.reviews_nav_graph)
        navController.setCurrentDestination(R.id.moviesSearchFragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.movieReviewFragment) {
                val context = ApplicationProvider.getApplicationContext<Context>()!!
                val keyMovieId = context.getString(R.string.movie_id_arg)
                val movieIdArg = navController
                    .currentBackStackEntry!!.arguments!!.getString(keyMovieId)!!
                val bundle = Bundle().apply { putString(keyMovieId,movieIdArg) }
                reviewScenario = launchFragmentInHiltContainer<MovieReviewFragment>(fragmentArgs = bundle)
                Shadows.shadowOf(Looper.getMainLooper()).idle()
            }
        }

        // Mock out image loader
        mockkObject(ImageLoader)
    }

    @Given("^Reviewed existing movies searched by \"([^\"]*)\"$")
    fun reviewed_existing_movies_searched_by_query(query: String) {
        dispatcher = TestDispatcher(query)
        server.setDispatcher(dispatcher)
    }

    @When("^User open movies search screen$")
    fun user_open_movies_search_screen() {
        searchScenario = launchFragmentInHiltContainer<MoviesSearchFragment>()

        // Set the NavController property on the fragment with test controller
        searchScenario.onActivity {
            Navigation.setViewNavController(
                it.supportFragmentManager.fragments[0].requireView(),
                navController)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @And("^Perform search for reviewed movies with \"([^\"]*)\" query$")
    fun perform_search_for_reviewed_movies_with_query(query: String) {
        onView(withHint(string.search_hint))
            .perform(typeText(query))
            .perform(pressImeActionButton())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @And("^Open first search resulted move read its review$")
    fun open_first_search_resulted_move_read_its_review() {
        onView(withId(R.id.search_results))
            .perform(
                actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                    0,
                    click()
                )
            )
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Movie review detail should be shown$")
    fun movie_review_detail_should_be_shown() {
        val review = expectedUiMovieReview()

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

    private fun expectedUiMovieReview(): UiMovieReviewData {
        val movieDetailJson = getJsonFromResource(dispatcher.movieDetailResourcePath)
        val movieTrailersJson = getJsonFromResource(dispatcher.movieTrailersResourcePath)
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
        private val searchResultsResourcePath = "json/themoviedb_movies_sorted_by_popularity.json"
        val movieDetailResourcePath = "json/themoviedb_movie_detail.json"
        val movieTrailersResourcePath = "json/themoviedb_movie_trailers.json"
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
                            .setBody(getJsonFromResource(searchResultsResourcePath))
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

    private data class UiMovieReviewData(val title: String,
                                 val rating: String,
                                 val genres: String,
                                 val releaseDate: String,
                                 val summary: String,
                                 val review: String,
                                 val backDropImageUrl: String,
                                 val trailersThumbnailUrls: List<String>)
}