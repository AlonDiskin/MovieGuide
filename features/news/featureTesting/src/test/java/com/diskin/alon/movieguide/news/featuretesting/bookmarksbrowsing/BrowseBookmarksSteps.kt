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
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.news.data.local.Bookmark
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.featuretesting.TestDatabase
import com.diskin.alon.movieguide.news.featuretesting.util.getJsonBodyFromResource
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
    private var selectedArticleId = ""

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
        database.testBookmarkDao().insert(*createBookmarks().toTypedArray())
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

    @Then("^All bookmarks are listed by 'news first' sorting$")
    fun all_bookmarks_are_listed_by_news_first_sorting() {
        verifyBookmarksShow(expectedUiBookmarksByNewest())
    }

    @When("^User select to sort bookmarks by \"([^\"]*)\"$")
    fun user_select_to_sort_bookmarks(sorting: String) {
        val context = getApplicationContext<Context>()
        val menuItem = when(sorting) {
            "newest" -> ActionMenuItem(
                context,
                0,
                id.action_sort_newest,
                0,
                0,
                null
            )

            "oldest" -> ActionMenuItem(
                context,
                0,
                id.action_sort_oldest,
                0,
                0,
                null
            )

            else -> throw IllegalArgumentException("Unknown scenario arg:${sorting}")
        }

        bookmarksFragmentScenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(menuItem)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // TODO find a way to sync test thread(main) with AsyncDiffer from ListAdapter
        Thread.sleep(500L)
    }

    @Then("^Bookmarks should be sorted as \"([^\"]*)\"$")
    fun bookmarks_should_be_sorted(sorting: String) {
        when(sorting) {
            "newest" -> {
                verifyBookmarksShow(expectedUiBookmarksByNewest())
            }

            "oldest" -> {
                verifyBookmarksShow(expectedUiBookmarksByOldest())
            }

            else -> throw IllegalArgumentException("Unknown scenario arg:${sorting}")
        }
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
        return listOf(
            Bookmark(
                dispatcher.oldestArticleResourceId,
                "title1",
                LocalDateTime(2020, 10, 23, 12, 35).toDate().time,
                "image_url1",
                "article_url1"
            ),
            Bookmark(
                "articleId",
                "title2",
                LocalDateTime(2020, 2, 17, 8, 5).toDate().time,
                "image_url2",
                "article_url2"
            ),
            Bookmark(
                dispatcher.newestArticleResourceId,
                "title3",
                LocalDateTime(2020, 11, 10, 22, 55).toDate().time,
                "image_url3",
                "article_url3"
            )
        )
    }

    private fun expectedUiBookmarksByNewest(): List<UiBookmark> {
        return database.testBookmarkDao()
            .getAll()
            .blockingFirst()
            .asReversed()
            .map {
                UiBookmark(
                    it.title,
                    LocalDateTime(it.date).toString("dd MMM HH:mm"),
                    it.imageUrl
                )
            }
    }

    private fun expectedUiBookmarksByOldest(): List<UiBookmark> {
        return database.testBookmarkDao()
            .getAll()
            .blockingFirst()
            .map {
                UiBookmark(
                    it.title,
                    LocalDateTime(it.date).toString("dd MMM HH:mm"),
                    it.imageUrl
                )
            }
    }

    private fun expectedUiArticle(): UiArticle {
        val json = getJsonBodyFromResource(dispatcher.getLastRequestedArticleResPath())
        val jsonArray = JSONArray(json)
        val jsonEntryObject = jsonArray.getJSONObject(0)!!

        return UiArticle(
            jsonEntryObject.getString("title"),
            jsonEntryObject.getString("author"),
            jsonEntryObject.getJSONObject("summary").getString("content"),
            LocalDateTime(jsonEntryObject.getLong("published")).toString("dd MMM HH:mm"),
            jsonEntryObject.getJSONObject("visual").getString("url")
        )
    }

    private class TestDispatcher: Dispatcher() {
        val oldestArticleResourcePath = "json/feedly_entry.json"
        val oldestArticleResourceId = "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e6c31519:81d950:5d3e1c98"
        val newestArticleResourcePath = "json/feedly_entry2.json"
        val newestArticleResourceId = "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e66eaaf4:7efce1:58da7475"
        private var lastRequestedArticlePath = ""

        override fun dispatch(request: RecordedRequest): MockResponse {
            val oldestArticlePath = "/entries/$oldestArticleResourceId"
            val newestArticlePath = "/entries/$newestArticleResourceId"


            return when(request.requestUrl.uri().path) {
                oldestArticlePath -> {
                    lastRequestedArticlePath = oldestArticleResourcePath
                    MockResponse()
                        .setBody(getJsonBodyFromResource(oldestArticleResourcePath))
                        .setResponseCode(200)
                }

                newestArticlePath -> {
                    lastRequestedArticlePath = newestArticleResourcePath
                    MockResponse()
                        .setBody(getJsonBodyFromResource(newestArticleResourcePath))
                        .setResponseCode(200)
                }

                else -> MockResponse().setResponseCode(404)
            }
        }

        fun getLastRequestedArticleResPath() = lastRequestedArticlePath
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