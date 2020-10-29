package com.diskin.alon.movieguide.news.featuretesting

import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.common.uitesting.swipeToRefresh
import com.diskin.alon.movieguide.news.presentation.DATE_FORMAT
import com.diskin.alon.movieguide.news.presentation.MoviesHeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.MoviesHeadlinesViewModelImpl.Companion.PAGE_SIZE
import com.diskin.alon.movieguide.news.presentation.NewsHeadline
import com.diskin.alon.movieguide.news.presentation.NewsHeadlinesAdapter.NewsHeadlineViewHolder
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.mockkObject
import io.mockk.verify
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.allOf
import org.joda.time.LocalDateTime
import org.json.JSONObject
import org.robolectric.Shadows

/**
 * Step definitions for 'Headlines are listed' scenario.
 */
class HeadlinesListedSteps(server: MockWebServer) : GreenCoffeeSteps() {

    companion object {
        // displayed items count should be equal to size of items in initial page size request
        // plus 1 page request,since we have a server that has 2 pages, 1 for initial request,
        // and one for 1 additional request. We expect to see all of them in ui
        private const val EXPECTED_UI_ITEMS_COUNT = (PAGE_SIZE * 3) + PAGE_SIZE
        private const val PAGES_NUM = 2
    }

    private lateinit var scenario: FragmentScenario<MoviesHeadlinesFragment>
    private val dispatcher = HeadlinesDispatcher()

    init {
        // Mock out ImageLoader for image loading verification
        mockkObject(ImageLoader)

        // Set dispatcher for this scenario testing
        server.setDispatcher(dispatcher)
    }

    @Given("^User opened news headlines screen$")
    fun userOpenedNewsHeadlinesScreen() {
        // launch movies headlines fragment
        scenario = FragmentScenario.launchInContainer(MoviesHeadlinesFragment::class.java)
    }

    @When("^User scrolls to bottom of ui$")
    fun userScrollsThroughUi() {
        // scroll to bottom
        scrollToBottom()
    }

    @Then("^All headlines should be listed sorted descending by date$")
    fun allHeadlinesShouldBeListedSortedDescendingByDate() {
        // Verify size of displayed data set is correct
        verifyShownHeadlinesListSize(EXPECTED_UI_ITEMS_COUNT)

        // Verify all server feed entries are displayed as expected
        verifyHeadlinesShown(getExpectedUiHeadlines())
    }

    @When("^User refresh headlines$")
    fun userRefreshHeadlines() {
        // refresh dispatcher
        dispatcher.refresh()

        onView(withId(R.id.swipe_refresh))
            .perform(swipeToRefresh())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Updated headlines should be shown$")
    fun updatedHeadlinesShouldBeShown() {
        // scroll to bottom
        scrollToBottom()

        // Verify size of displayed data set is correct
        verifyShownHeadlinesListSize(EXPECTED_UI_ITEMS_COUNT)

        // Verify all server feed entries are displayed as expected
        verifyHeadlinesShown(getExpectedRefreshedUiHeadlines())
    }

    private fun verifyShownHeadlinesListSize(expectedSize: Int) {
        scenario.onFragment { fragment ->
            val recycler = fragment.view!!.findViewById<RecyclerView>(R.id.headlines)
            assertThat(recycler.adapter!!.itemCount).isEqualTo(expectedSize)
        }
    }

    private fun scrollToBottom() {
        for (i in 0 until PAGES_NUM) {
            scenario.onFragment { fragment ->
                val recycler = fragment.view!!.findViewById<RecyclerView>(R.id.headlines)
                val lastPosition = recycler.adapter!!.itemCount
                recycler.smoothScrollToPosition(lastPosition)
            }

            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
    }

    private fun verifyHeadlinesShown(headlines: List<NewsHeadline>) {
        // Verify all server feed entries are displayed as expected
        headlines.forEachIndexed { i, headline ->
            // Scroll to expected headline layout position
            onView(withId(R.id.headlines))
                .perform(scrollToPosition<NewsHeadlineViewHolder>(i))

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

    private fun getExpectedUiHeadlines(): List<NewsHeadline> {
        val initialPageJson = getJsonBodyFromResource("feed_init_page.json")
        val lastPageJson = getJsonBodyFromResource("feed_last_page.json")
        val initialPageHeadlines = parseFeedlyResponseJsonToNewsHeadlines(initialPageJson)
        val lastPageHeadlines = parseFeedlyResponseJsonToNewsHeadlines(lastPageJson)
        val result = mutableListOf<NewsHeadline>()

        result.addAll(initialPageHeadlines)
        result.addAll(lastPageHeadlines)
        return result
    }

    private fun getExpectedRefreshedUiHeadlines(): List<NewsHeadline> {
        val initialPageJson = getJsonBodyFromResource("refresh_feed_init_page.json")
        val lastPageJson = getJsonBodyFromResource("refresh_feed_last_page.json")
        val initialPageHeadlines = parseFeedlyResponseJsonToNewsHeadlines(initialPageJson)
        val lastPageHeadlines = parseFeedlyResponseJsonToNewsHeadlines(lastPageJson)
        val result = mutableListOf<NewsHeadline>()

        result.addAll(initialPageHeadlines)
        result.addAll(lastPageHeadlines)
        return result
    }

    private fun parseFeedlyResponseJsonToNewsHeadlines(json: String): List<NewsHeadline> {
        val jsonResponseObject = JSONObject(json)
        val jsonItemsArray = jsonResponseObject.getJSONArray("items")
        val newsHeadlines = mutableListOf<NewsHeadline>()

        for (i in 0 until jsonItemsArray.length()) {
            val jsonItemObject = jsonItemsArray.getJSONObject(i)
            newsHeadlines.add(
                NewsHeadline(
                    jsonItemObject.getString("id"),
                    jsonItemObject.getString("title"),
                    LocalDateTime(jsonItemObject.getLong("published")).toString(DATE_FORMAT),
                    jsonItemObject.getJSONObject("visual").getString("url")
                )
            )
        }

        return newsHeadlines
    }
}