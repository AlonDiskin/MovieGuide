package com.diskin.alon.movieguide.journeysteps

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.diskin.alon.movieguide.R
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesAdapter.HeadlineViewHolder
import com.diskin.alon.movieguide.util.DeviceUtil
import com.diskin.alon.movieguide.util.FileUtil
import com.diskin.alon.movieguide.util.isRecyclerViewItemsCount
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.joda.time.LocalDateTime
import org.json.JSONArray
import java.net.URLEncoder

/**
 * Step definitions for 'User bookmarks article' scenario.
 */
class ArticleBookmarkingSteps(server: MockWebServer): GreenCoffeeSteps() {

    private val dispatcher = TestDispatcher()

    init {
        // Prepare test server
        server.setDispatcher(dispatcher)
    }

    @Given("^User launched app from device home$")
    fun user_launched_app_from_device_home() {
        DeviceUtil.openDeviceHome()
        DeviceUtil.launchApp()
    }

    @And("^Open news screen$")
    fun open_news_screen() {
        onView(withId(R.id.news))
            .perform(click())
    }

    @When("^User bookmarks first listed article$")
    fun user_bookmarks_first_listed_article() {
        onView(withId(R.id.headlines))
            .perform(
                actionOnItemAtPosition<HeadlineViewHolder>(
                    0,
                    click()
                )
            )
        onView(withId(R.id.action_bookmarking))
            .perform(click())
    }

    @And("^Open bookmarks screen$")
    fun open_bookmarks_screen() {
        // Use Espresso 'press back' and not DeviceUtil.since uiautomator is not synced with test thread
        pressBack()
        onView(withId(R.id.bookmarks))
            .perform(click())
    }

    @Then("^Bookmarked article should be listed$")
    fun bookmarked_article_should_be_listed() {
        val bookmark = expectedUiBookmark()

        onView(withId(R.id.bookmarked_articles))
            .check(matches(isRecyclerViewItemsCount(1)))
        onView(withId(R.id.title))
            .check(matches(withText(bookmark.title)))
        onView(withId(R.id.published))
            .check(matches(withText(bookmark.date)))

    }

    private fun expectedUiBookmark(): UiBookmark {
        val json = FileUtil.readStringFromFile(dispatcher.bookmarkedArticleResource)
        val jsonArray = JSONArray(json)
        val articleObject = jsonArray.getJSONObject(0)

        return UiBookmark(
            articleObject.getString("title"),
            LocalDateTime(articleObject.getLong("published")).toString("dd MMM HH:mm")
        )
    }

    private class TestDispatcher: Dispatcher() {
        val articlesResource = "assets/json/feedly_movie_news_stream.json"
        val bookmarkedArticleResource = "assets/json/feedly_movie_news_entry.json"
        private val feedPath = "/streams/contents"
        private val dynamicEntriesPath = "/entries/.mget"
        private val entryPath = "/entries/".plus(
            URLEncoder.encode(
                "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e6c31519:81d950:5d3e1c98",
                "UTF-8"
            )
        )

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.requestUrl.url().path) {
                feedPath -> MockResponse()
                    .setBody(FileUtil.readStringFromFile(articlesResource))
                    .setResponseCode(200)

                entryPath -> MockResponse()
                    .setBody(FileUtil.readStringFromFile(bookmarkedArticleResource))
                    .setResponseCode(200)

                dynamicEntriesPath -> MockResponse()
                    .setBody(FileUtil.readStringFromFile(bookmarkedArticleResource))
                    .setResponseCode(200)

                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    private data class UiBookmark(val title: String,val date: String)
}