package com.diskin.alon.movieguide.news.featuretesting.listarticles

import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.common.uitesting.swipeToRefresh
import com.diskin.alon.movieguide.news.data.remote.*
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.featuretesting.util.parseFeedlyResponseJsonToNewsHeadlines
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesAdapter.HeadlineViewHolder
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.viewmodel.HeadlinesViewModelImpl.Companion.PAGE_SIZE
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
 * Step definitions for 'Articles are listed' scenario.
 */
class ListArticlesSteps(server: MockWebServer) : GreenCoffeeSteps() {

    companion object {
        // displayed items count should be equal to size of items in initial page size request
        // plus 1 page request,since we have a server that has 2 pages, 1 for initial request,
        // and one for 1 additional request. We expect to see all of them in ui
        private const val EXPECTED_UI_ITEMS_COUNT = (PAGE_SIZE * 3) + PAGE_SIZE
        private const val PAGES_NUM = 2
    }

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private val dispatcher = ArticlesDispatcher()

    init {
        // Mock out ImageLoader for image loading verification
        mockkObject(ImageLoader)

        // Set dispatcher for this scenario testing
        server.setDispatcher(dispatcher)
    }

    @Given("^User opened articles screen$")
    fun userOpenedArticlesScreen() {
        // launch movies headlines fragment
        scenario = launchFragmentInHiltContainer<HeadlinesFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User scrolls to bottom of ui$")
    fun userScrollsThroughUi() {
        // scroll to bottom
        scrollToBottom()
    }

    @Then("^All articles should be listed sorted descending by date$")
    fun allArticlesShouldBeListedSortedDescendingByDate() {
        // Verify size of displayed data set is correct
        verifyShownHeadlinesListSize(EXPECTED_UI_ITEMS_COUNT)

        // Verify all server feed entries are displayed as expected
        verifyHeadlinesShown(getExpectedUiHeadlines())
    }

    @When("^User refresh content$")
    fun userRefreshContent() {
        // refresh dispatcher
        dispatcher.refresh()

        onView(withId(R.id.swipe_refresh))
            .perform(swipeToRefresh())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Updated articles should be shown$")
    fun updatedArticlesShouldBeShown() {
        // scroll to bottom
        scrollToBottom()

        // Verify size of displayed data set is correct
        verifyShownHeadlinesListSize(EXPECTED_UI_ITEMS_COUNT)

        // Verify all server feed entries are displayed as expected
        verifyHeadlinesShown(getExpectedRefreshedUiHeadlines())
    }

    private fun verifyShownHeadlinesListSize(expectedSize: Int) {
        scenario.onActivity { activity ->
            val recycler = activity.findViewById<RecyclerView>(R.id.headlines)
            assertThat(recycler.adapter!!.itemCount).isEqualTo(expectedSize)
        }
    }

    private fun scrollToBottom() {
        for (i in 0 until PAGES_NUM) {
            scenario.onActivity { activity ->
                val recycler = activity.findViewById<RecyclerView>(R.id.headlines)
                val lastPosition = recycler.adapter!!.itemCount
                recycler.smoothScrollToPosition(lastPosition)
            }

            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
    }

    private fun verifyHeadlinesShown(headlines: List<Headline>) {
        // Verify all server feed entries are displayed as expected
        headlines.forEachIndexed { i, headline ->
            // Scroll to expected headline layout position
            onView(withId(R.id.headlines))
                .perform(scrollToPosition<HeadlineViewHolder>(i))

            Shadows.shadowOf(Looper.getMainLooper()).idle()

            // Check list item at scrolled position display expected data
            onView(withRecyclerView(R.id.headlines).atPosition(i))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.published),
                                withText(headline.date)
                            )
                        )
                    )
                )

            onView(withRecyclerView(R.id.headlines).atPosition(i))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.title),
                                withText(headline.title),
                                isDisplayed()
                            )
                        )
                    )
                )

            // Verify news headline image was loaded
            verify { ImageLoader.loadIntoImageView(any(), headline.imageUrl) }
        }
    }

    private fun getExpectedUiHeadlines(): List<Headline> {
        val initialPageJson = getJsonFromResource("json/feed_init_page.json")
        val lastPageJson = getJsonFromResource("json/feed_last_page.json")
        val initialPageHeadlines = parseFeedlyResponseJsonToNewsHeadlines(initialPageJson)
        val lastPageHeadlines = parseFeedlyResponseJsonToNewsHeadlines(lastPageJson)
        val result = mutableListOf<Headline>()

        result.addAll(initialPageHeadlines)
        result.addAll(lastPageHeadlines)
        return result
    }

    private fun getExpectedRefreshedUiHeadlines(): List<Headline> {
        val initialPageJson = getJsonFromResource("json/refresh_feed_init_page.json")
        val lastPageJson = getJsonFromResource("json/refresh_feed_last_page.json")
        val initialPageHeadlines = parseFeedlyResponseJsonToNewsHeadlines(initialPageJson)
        val lastPageHeadlines = parseFeedlyResponseJsonToNewsHeadlines(lastPageJson)
        val result = mutableListOf<Headline>()

        result.addAll(initialPageHeadlines)
        result.addAll(lastPageHeadlines)
        return result
    }

    private class ArticlesDispatcher : Dispatcher() {

        companion object {
            private const val INIT_PAGE_SIZE = PAGE_SIZE * 3
            private const val PAGE_KEY = "174d587cb4b:149a2e0:5d3e1c98"
            private const val REFRESH_PAGE_KEY = "174daad7957:4e7e98:5d3e1c98"
            private const val INITIAL_PAGE_PATH = "/$FEEDLY_FEED_PATH?" +
                    "$FEEDLY_FEED_ID_PARAM=$MOVIES_NEWS_FEED&" +
                    "$FEEDLY_FEED_SIZE_PARAM=$INIT_PAGE_SIZE"
            private const val LAST_PAGE_PATH = "/$FEEDLY_FEED_PATH?" +
                    "$FEEDLY_FEED_ID_PARAM=$MOVIES_NEWS_FEED&" +
                    "$FEEDLY_PAGE_KEY_PARAM=$PAGE_KEY&" +
                    "$FEEDLY_FEED_SIZE_PARAM=$PAGE_SIZE"
            private const val REFRESH_LAST_PAGE_PATH = "/$FEEDLY_FEED_PATH?" +
                    "$FEEDLY_FEED_ID_PARAM=$MOVIES_NEWS_FEED&" +
                    "$FEEDLY_PAGE_KEY_PARAM=$REFRESH_PAGE_KEY&" +
                    "$FEEDLY_FEED_SIZE_PARAM=$PAGE_SIZE"
        }

        private var pageSources = Pair("json/feed_init_page.json","json/feed_last_page.json")

        override fun dispatch(request: RecordedRequest): MockResponse {
            val decodedPath = "${request.requestUrl.url().path}?${request.requestUrl.query()}"
            return when(decodedPath) {
                INITIAL_PAGE_PATH -> {
                    MockResponse().setResponseCode(200)
                        .setBody(getJsonFromResource(pageSources.first))
                }

                LAST_PAGE_PATH -> {
                    MockResponse().setResponseCode(200)
                        .setBody(getJsonFromResource(pageSources.second))
                }

                REFRESH_LAST_PAGE_PATH -> {
                    MockResponse().setResponseCode(200)
                        .setBody(getJsonFromResource(pageSources.second))
                }

                else -> throw IllegalArgumentException("unexpected request")
            }
        }

        fun refresh() {
            pageSources = Pair("json/refresh_feed_init_page.json", "json/refresh_feed_last_page.json")
        }
    }
}