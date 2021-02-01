package com.diskin.alon.movieguide.news.featuretesting.bookmarkarticle

import android.content.Context
import android.os.Bundle
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.news.featuretesting.di.TestDatabase
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.controller.ArticleFragment
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.robolectric.Shadows
import java.net.URLEncoder

/**
 * Step definitions for 'User bookmark article' scenario.
 */
class BookmarkArticleSteps(
    server: MockWebServer,
    private val database: TestDatabase
) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private val dispatcher = TestDispatcher()

    init {

        // Prepare test server
        server.setDispatcher(dispatcher)
    }

    @Given("^User open unbookmarked article for reading$")
    fun user_open_unbookmarked_article_for_reading() {
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val bundle = Bundle().apply {
            putString(context.getString(R.string.key_article_id),dispatcher.entryId)
        }
        scenario = launchFragmentInHiltContainer<ArticleFragment>(fragmentArgs = bundle)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User select to bookmark article$")
    fun user_select_to_bookmark_article() {
        onView(withId(R.id.action_bookmarking))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Article should be shown as bookmarked$")
    fun article_should_be_shown_as_bookmarked() {
        onView(withId(R.id.action_bookmarking))
            .check(
                matches(
                    withContentDescription(
                        R.string.title_action_unbookmark
                    )
                )
            )
    }

    @And("^App should bookmark article$")
    fun app_should_bookmark_article() {
        val bookmarks = database.bookmarkDao().getAll().blockingFirst()

        assertThat(bookmarks.size).isEqualTo(1)
        assertThat(bookmarks.any { it.articleId == dispatcher.entryId }).isTrue()
    }

    @When("^User select to un bookmark article$")
    fun user_select_to_un_bookmark_article() {
        onView(withId(R.id.action_bookmarking))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Article should be shown as not bookmarked$")
    fun article_should_be_shown_as_not_bookmarked() {
        onView(withId(R.id.action_bookmarking))
            .check(
                matches(
                    withContentDescription(
                        R.string.title_action_bookmark
                    )
                )
            )
    }

    @And("^App should remove article from bookmarks$")
    fun app_should_remove_article_from_bookmarks() {
        val bookmarks = database.bookmarkDao().getAll().blockingFirst()

        assertThat(bookmarks.size).isEqualTo(0)
    }

    private class TestDispatcher : Dispatcher() {
        val entryResource = "json/feedly_entry.json"
        val entryId = "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e6c31519:81d950:5d3e1c98"
        private val entryPath = "/entries/".plus(
            URLEncoder.encode(
                entryId,
                "UTF-8"
            )
        )

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.path) {
                entryPath -> MockResponse()
                    .setBody(getJsonFromResource(entryResource))
                    .setResponseCode(200)

                else -> MockResponse().setResponseCode(404)
            }
        }

    }
}