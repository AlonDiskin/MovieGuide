package com.diskin.alon.movieguide.news.featuretesting.bookmarksbrowsing

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.featuretesting.TestDatabase
import com.diskin.alon.movieguide.news.presentation.R.id
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
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
 * Step definitions for 'User browse bookmarked news' scenario.
 */
class BrowseBookmarksSteps(
    server: MockWebServer,
    private val database: TestDatabase
) : GreenCoffeeSteps() {

    private lateinit var bookmarksFragmentScenario: FragmentScenario<BookmarksFragment>
    private lateinit var articleActivityScenario: ActivityScenario<ArticleActivity>
    private val navController = TestNavHostController(getApplicationContext())
    private val dispatcher = TestDispatcher()

    init {
        // Set test nav controller
        navController.setGraph(R.navigation.bookmarks_nav_graph)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.articleActivity) {
                val context = getApplicationContext<Context>()!!
                val keyArticleId = context.getString(R.string.key_article_id)
                val articleIdArg = navController
                    .currentBackStackEntry!!.arguments!!.getString(keyArticleId)!!
                val intent = Intent(context, ArticleActivity::class.java)
                    .apply { putExtra(keyArticleId, articleIdArg) }

                // Launch article detail activity when user navigates to it from fragment
                // (manually,robolectric bug)
                articleActivityScenario = ActivityScenario.launch(intent)

                // Wait for main looper to idle
                Shadows.shadowOf(Looper.getMainLooper()).idle()
            }
        }

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
        bookmarksFragmentScenario = FragmentScenario.launchInContainer(
            BookmarksFragment::class.java,
            null,
            R.style.AppTheme,
            null
        )
        // Set test nav controller on headlines fragment
        bookmarksFragmentScenario.onFragment { Navigation.setViewNavController(
            it.requireView(),
            navController
        ) }
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

    @When("^User select to read the first bookmark$")
    fun user_select_to_read_first_bookmark_bookmark() {
        onView(withId(R.id.bookmarks))
            .perform(
                actionOnItemAtPosition<BookmarksAdapter.BookmarkViewHolder>(
                    0,
                    click()
                )
            )
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Bookmarked news article should be shown$")
    fun bookmarked_news_article_should_be_shown() {
        val article = expectedUiArticle()
        onView(withId(R.id.title))
            .check(matches(withText(article.title)))

        onView(withId(R.id.published))
            .check(matches(withText(article.date)))

        onView(withId(R.id.author))
            .check(matches(withText(article.author)))

        onView(withId(R.id.content))
            .check(matches(withText(article.content)))

        verify { ImageLoader.loadIntoImageView(any(), article.imageUrl) }
    }

    private fun verifyBookmarksShow(bookmarks: List<UiBookmark>) {
        bookmarksFragmentScenario.onFragment { fragment ->
            val adapter =
                fragment.view!!.findViewById<RecyclerView>(R.id.bookmarks).adapter!!

            assertThat(adapter.itemCount).isEqualTo(bookmarks.size)
        }

        bookmarks.forEachIndexed { index, uiBookmark ->
            onView(withId(id.bookmarks))
                .perform(
                    scrollToPosition<BookmarksAdapter.BookmarkViewHolder>(
                        index
                    )
                )

            Shadows.shadowOf(Looper.getMainLooper()).idle()

            onView(
                withRecyclerView(id.bookmarks)
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

    private fun expectedUiArticle(): UiArticle {
         val articleId = database.testBookmarkDao()
             .getAll()
             .blockingFirst()
             .first()
             .articleId
        val entryJson = getJsonFromResource(dispatcher.entriesResources.getValue(articleId))
        val entryJsonObject = JSONObject(entryJson)

        return UiArticle(
            entryJsonObject.getString("title"),
            entryJsonObject.getString("author"),
            entryJsonObject.getJSONObject("summary").getString("content"),
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
                "json/feedly_entry.json"
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

                "GET" -> {
                    if (entriesResources.keys.contains(request.requestUrl.pathSegments().last())) {
                        buildEntryGetResponse(request)

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

        private fun buildEntryGetResponse(request: RecordedRequest): MockResponse {
            val entryPath: String = entriesResources.getValue(request.requestUrl.pathSegments().last())
            val entryJson = getJsonFromResource(entryPath)
            val responseJson = JSONArray()
            return MockResponse()
                .setBody(responseJson.put(JSONObject(entryJson)).toString())
                .setResponseCode(200)
        }
    }

    private data class UiBookmark(val title: String, val date: String, val imageUrl: String)

    private data class UiArticle(
        val title: String,
        val author: String,
        val content: String,
        val date: String,
        val imageUrl: String
    )
}