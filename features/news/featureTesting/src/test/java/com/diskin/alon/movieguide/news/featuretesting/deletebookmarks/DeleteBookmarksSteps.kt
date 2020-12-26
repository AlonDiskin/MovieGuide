package com.diskin.alon.movieguide.news.featuretesting.deletebookmarks

import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.featuretesting.TestDatabase
import com.diskin.alon.movieguide.news.presentation.R.id
import com.diskin.alon.movieguide.news.presentation.R.string
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksAdapter
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksFragment
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
import org.json.JSONArray
import org.json.JSONObject
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowAlertDialog

/**
 * Step definitions for 'Bookmarks deleted' scenario.
 */
class DeleteBookmarksSteps(
    server: MockWebServer,
    private val database: TestDatabase
) : GreenCoffeeSteps() {

    private lateinit var scenario: FragmentScenario<BookmarksFragment>
    private val dispatcher = TestDispatcher()

    init {
        // Prepare test server
        server.setDispatcher(dispatcher)
    }

    @Given("^User has previously bookmarked articles$")
    fun user_has_previously_bookmarked_articles() {
        createBookmarks().forEach { database.testBookmarkDao().insert(it) }
    }

    @And("^User open bookmarks screen$")
    fun user_open_bookmarks_screen() {
        scenario = FragmentScenario.launchInContainer(
            BookmarksFragment::class.java,
            null,
            R.style.AppTheme,
            null
        )
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User select to delete all bookmarks$")
    fun user_select_to_delete_all_bookmarks() {
        val count = this.database.bookmarkDao().getAll().blockingFirst().size

        for (i in 0 until count) {
            onView(withId(id.bookmarks))
                .perform(
                    scrollToPosition<BookmarksAdapter.BookmarkViewHolder>(i)
                )

            onView(withRecyclerView(id.bookmarks).atPosition(i))
                .perform(if (i == 0) longClick() else click())
        }

        onView(withContentDescription(string.title_action_remove_bookmark))
            .perform(click())

        val dialog = (ShadowAlertDialog.getLatestDialog() as AlertDialog)

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Bookmarks screen should be updated to show no items$")
    fun bookmarks_screen_should_be_updated_to_show_no_items() {
        scenario.onFragment { fragment ->
            val recycler = fragment.view!!.findViewById<RecyclerView>(R.id.bookmarks)
            assertThat(recycler.adapter!!.itemCount).isEqualTo(0)
        }
    }

    @And("^All bookmarks should be deleted$")
    fun all_bookmarks_should_be_deleted() {
        assertThat(this.database.bookmarkDao().getAll().blockingFirst().size).isEqualTo(0)
    }

    private fun createBookmarks(): List<Bookmark> {
        return dispatcher.entriesResources.keys.map { Bookmark(it) }
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

                else -> {
                    MockResponse().setResponseCode(404)
                }
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
}