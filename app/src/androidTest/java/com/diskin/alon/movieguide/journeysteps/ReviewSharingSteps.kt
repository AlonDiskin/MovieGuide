package com.diskin.alon.movieguide.journeysteps

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.diskin.alon.movieguide.R
import com.diskin.alon.movieguide.reviews.presentation.R.id
import com.diskin.alon.movieguide.reviews.presentation.R.string
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesAdapter.MovieViewHolder
import com.diskin.alon.movieguide.util.DeviceUtil
import com.diskin.alon.movieguide.util.FileReader
import com.diskin.alon.movieguide.util.MockWebServerDispatcher
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.json.JSONObject

/**
 * Step definitions for 'User share review' scenario.
 */
class ReviewSharingSteps : GreenCoffeeSteps() {

    @Given("^User launched app from device home$")
    fun userLaunchedAppFromDeviceHome() {
        DeviceUtil.openDeviceHome()
        DeviceUtil.launchApp()
    }

    @And("^Opened movie reviews screen$")
    fun openedMovieReviewsScreen() {
        onView(withId(R.id.reviews))
            .perform(click())
    }

    @When("^User selects to read first listed review$")
    fun userSelectsToReadFirstListedReview() {
        onView(withId(R.id.movies))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<MovieViewHolder>(
                    0,
                    click()
                )
            )
    }

    @And("^Read movie review$")
    fun readMovieReview() {
        val review = getExpectedUiReviewData()

        onView(withId(id.title))
            .check(matches(withText(review.title)))

        onView(withId(id.rating))
            .check(matches(withText(review.rating)))

        onView(withId(id.genre))
            .check(matches(withText(review.genres)))

        onView(withId(id.release_date))
            .check(matches(withText(review.releaseDate)))

        onView(withId(id.summary))
            .check(matches(withText(review.summary)))

        onView(withId(id.review))
            .check(matches(withText(review.review)))
    }

    @And("^User select to share review$")
    fun userSelectToShareReview() {
        Intents.init()

        onView(withId(R.id.action_share))
            .perform(click())
    }

    @Then("^Review should be shared$")
    fun reviewShouldBeShared() {
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val review = getExpectedUiReviewData()

        assertThat(intent.type)
            .isEqualTo(context.getString(string.mime_type_text))
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT))
            .isEqualTo(review.trailersUrls.first())

        Intents.release()
        DeviceUtil.pressBack()
    }

    private fun getExpectedUiReviewData(): UiReview {
        val movieDetailJson = FileReader.readStringFromFile(MockWebServerDispatcher.MOVIE_DETAIL_RES)
        val movieTrailersJson = FileReader.readStringFromFile(MockWebServerDispatcher.MOVIE_TRAILERS_RES)
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

        return UiReview(
            jsonDetailObject.getString("title"),
            jsonDetailObject.getDouble("vote_average").toString(),
            genres,
            LocalDateTime(releaseDate).toString("dd MMM yyyy"),
            jsonDetailObject.getString("overview"),
            "review_stub",
            trailersUrls
        )
    }

    private data class UiReview(val title: String,
                                val rating: String,
                                val genres: String,
                                val releaseDate: String,
                                val summary: String,
                                val review: String,
                                val trailersUrls: List<String>)
}