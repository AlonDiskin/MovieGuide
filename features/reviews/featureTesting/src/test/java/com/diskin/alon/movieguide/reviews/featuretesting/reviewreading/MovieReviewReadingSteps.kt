package com.diskin.alon.movieguide.reviews.featuretesting.reviewreading

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.featuretesting.R
import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesAdapter.MovieViewHolder
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
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
import org.robolectric.Shadows

/**
 * Step definitions for 'Movie review is read by user' scenario.
 */
class MovieReviewReadingSteps(server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var moviesFragmentScenario: FragmentScenario<MoviesFragment>
    private lateinit var reviewActivityScenario: ActivityScenario<MovieReviewActivity>
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    private val dispatcher = TestDispatcher()

    init {
        // Setup nav controller
        navController.setGraph(R.navigation.reviews_nav_graph)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.movieReviewActivity) {
                val context = ApplicationProvider.getApplicationContext<Context>()!!
                val keyMovieId = context.getString(R.string.movie_id_arg)
                val movieIdArg = navController
                    .currentBackStackEntry!!.arguments!!.getString(keyMovieId)!!
                val intent = Intent(context,MovieReviewActivity::class.java)
                    .apply { putExtra(keyMovieId,movieIdArg) }

                // Launch review detail activity when user navigates to it from fragment
                // (manually,robolectric bug)
                reviewActivityScenario = ActivityScenario.launch(intent)
                // Wait for main looper to idle
                Shadows.shadowOf(Looper.getMainLooper()).idle()
            }
        }

        // Prepare test server
        server.setDispatcher(dispatcher)

        // Mock out image loader
        mockkObject(ImageLoader)
    }

    @Given("^User opened movies screen$")
    fun userOpenedMoviesScreen() {
        moviesFragmentScenario = FragmentScenario.launchInContainer(MoviesFragment::class.java)
        // Set test nav controller on movies fragment
        moviesFragmentScenario.onFragment { Navigation.setViewNavController(it.requireView(), navController) }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User selects first shown movie$")
    fun userSelectsFirstShownMovie() {
        onView(withId(R.id.movies))
            .perform(actionOnItemAtPosition<MovieViewHolder>(0, click()))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Review screen should be opened$")
    fun reviewScreenShouldBeOpened() {
        onView(withId(R.id.root_movie_review))
            .check(matches(isDisplayed()))
    }

    @And("^Review detail should be shown$")
    fun reviewDetailShouldBeShown() {
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

        onView(withId(R.id.review_text))
            .check(matches(withText(review.review)))

        verify { ImageLoader.loadIntoImageView(any(),review.backDropImageUrl) }

        review.trailersThumbnailUrls.forEach { url ->
            verify { ImageLoader.loadIntoImageView(any(),url) }
        }
    }

    private class TestDispatcher : Dispatcher() {
        private val moviesResourcePath = "json/themoviedb_movies_sorted_by_popularity.json"
        val movieDetailResourcePath = "json/themoviedb_movie_detail.json"
        val movieTrailersResourcePath = "json/themoviedb_movie_trailers.json"
        private val moviesPath = "/3/discover/movie"
        private val movieDetailPath = "/3/movie/724989"
        private val movieTrailersPath = "/3/movie/724989/videos"

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.requestUrl.uri().path) {
                moviesPath -> {
                    return if (
                        request.requestUrl.queryParameter("page") == "1" &&
                        request.requestUrl.queryParameter("include_video") == "false" &&
                        request.requestUrl.queryParameter("include_adult") == "false" &&
                        request.requestUrl.queryParameter("sort_by") == "popularity.desc" &&
                        request.requestUrl.queryParameter("language") == "en" &&
                        request.requestUrl.queryParameter("api_key") == BuildConfig.MOVIE_DB_API_KEY
                    ) {
                        MockResponse()
                            .setBody(getJsonFromResource(moviesResourcePath))
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
}