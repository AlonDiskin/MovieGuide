package com.diskin.alon.movieguide.news.featuretesting.listbookmarks

import android.content.Context
import android.os.Looper
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.featuretesting.TestDatabase
import com.diskin.alon.movieguide.news.presentation.R.id
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksAdapter
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksFragment
import com.google.common.truth.Truth.assertThat
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
import org.joda.time.LocalDateTime
import org.json.JSONArray
import org.json.JSONObject
import org.robolectric.Shadows

/**
 * Step definitions for 'Bookmarks listed' scenario.
 */
class ListBookmarksSteps(
    server: MockWebServer,
    private val database: TestDatabase
) : GreenCoffeeSteps() {

    private lateinit var bookmarksFragmentScenario: FragmentScenario<BookmarksFragment>
    private val dispatcher = TestDispatcher()

    init {
        // Prepare test server
        server.setDispatcher(dispatcher)

        // Mock out image loader
        mockkObject(ImageLoader)
    }

    @Given("^User has previously bookmarked news$")
    fun user_has_previously_bookmarked_news() {
        createBookmarks().forEach { database.testBookmarkDao().insert(it) }
    }

    @And("^User open bookmarks screen$")
    fun user_open_bookmarks_screen() {
        bookmarksFragmentScenario = FragmentScenario.launchInContainer(BookmarksFragment::class.java)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^All bookmarks are listed by newest first$")
    fun all_bookmarks_are_listed_by_newest_first() {
        verifyBookmarksShow(expectedUiBookmarksByNewest())
    }

    @When("^User select to sort bookmarks by oldest first$")
    fun user_select_to_sort_bookmarks_by_oldest() {
        val context = getApplicationContext<Context>()
        val menuItem = ActionMenuItem(
            context,
            0,
            id.action_sort_oldest,
            0,
            0,
            null
        )

        bookmarksFragmentScenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(menuItem)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // TODO find a way to sync test thread(main) with AsyncDiffer from ListAdapter
        Thread.sleep(500L)
    }

    @Then("^Bookmarks should be sorted by oldest$")
    fun bookmarks_should_be_sorted_by_oldest() {
        verifyBookmarksShow(expectedUiBookmarksByOldest())
    }

    private fun verifyBookmarksShow(bookmarks: List<UiBookmark>) {
        bookmarksFragmentScenario.onFragment { fragment ->
            val adapter =
                fragment.view!!.findViewById<RecyclerView>(R.id.bookmarked_articles).adapter!!

            assertThat(adapter.itemCount).isEqualTo(bookmarks.size)
        }

        bookmarks.forEachIndexed { index, uiBookmark ->
            onView(withId(id.bookmarked_articles))
                .perform(
                    scrollToPosition<BookmarksAdapter.BookmarkViewHolder>(
                        index
                    )
                )

            Shadows.shadowOf(Looper.getMainLooper()).idle()

            onView(
                withRecyclerView(id.bookmarked_articles)
                    .atPosition(index)
            )
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(id.published),
                                withText(uiBookmark.date)
                            )
                        )
                    )
                )
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(id.title),
                                withText(uiBookmark.title),
                                isDisplayed()
                            )
                        )
                    )
                )

            verify { ImageLoader.loadIntoImageView(any(), uiBookmark.imageUrl) }
        }
    }

    private fun createBookmarks(): List<Bookmark> {
        return dispatcher.entriesResources.keys.map { Bookmark(it) }
    }

    private fun expectedUiBookmarksByNewest(): List<UiBookmark> {
        return database.testBookmarkDao()
            .getAll()
            .blockingFirst()
            .asReversed()
            .map(::expectedUiBookmark)
    }

    private fun expectedUiBookmarksByOldest(): List<UiBookmark> {
        return database.testBookmarkDao()
            .getAll()
            .blockingFirst()
            .map(::expectedUiBookmark)
    }

    private fun expectedUiBookmark(bookmark: Bookmark): UiBookmark {
        val entryJson = getJsonFromResource(dispatcher.entriesResources.getValue(bookmark.articleId))
        val entryJsonObject = JSONObject(entryJson)

        return UiBookmark(
            entryJsonObject.getString("title"),
            LocalDateTime(entryJsonObject.getLong("published")).toString("dd MMM HH:mm"),
            entryJsonObject.getJSONObject("visual").getString("url")
        )
    }

    private class TestDispatcher: Dispatcher() {
        private val _entriesResources: MutableMap<String,String> = HashMap()
        val entriesResources: Map<String,String> = _entriesResources

        init {
            _entriesResources.put(
                "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e6c31519:81d950:5d3e1c98",
                "json/feedly_entry1.json"
            )
            _entriesResources.put(
                "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e66eaaf4:7efce1:58da7475",
                "json/feedly_entry2.json"
            )
            _entriesResources.put(
                "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174d698e29e:17ba657:951e5be4",
                "json/feedly_entry3.json"
            )
        }

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.method) {
                "POST" -> {
                    if (request.requestUrl.uri().path == "/entries/.mget") {
                        buildEntriesPostResponse(request)

                    } else {
                        MockResponse().setResponseCode(404)
                    }
                }

                else -> MockResponse().setResponseCode(404)
            }
        }

        private fun buildEntriesPostResponse(request: RecordedRequest): MockResponse {
            val requestIdsJsonArray = JSONArray(request.body.readUtf8())
            val responseJson = JSONArray()

            for (i in 0 until requestIdsJsonArray.length()) {
                val requestedEntryId = requestIdsJsonArray.getJSONObject(i).getString("id")
                val entryPath = _entriesResources[requestedEntryId]!!
                val entryJson = getJsonFromResource(entryPath)
                val entryJsonObject = JSONObject(entryJson)

                responseJson.put(entryJsonObject)
            }

            return MockResponse()
                .setBody(responseJson.toString())
                .setResponseCode(200)
        }
    }

    private data class UiBookmark(val title: String, val date: String, val imageUrl: String)
}