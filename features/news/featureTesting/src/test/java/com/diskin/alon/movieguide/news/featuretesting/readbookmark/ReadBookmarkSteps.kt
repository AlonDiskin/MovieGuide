package com.diskin.alon.movieguide.news.featuretesting.readbookmark

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.featuretesting.TestDatabase
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksAdapter
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksFragment
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
import org.joda.time.LocalDateTime
import org.json.JSONArray
import org.json.JSONObject
import org.robolectric.Shadows

/**
 * Step definitions for 'User read bookmarked article' scenario.
 */
class ReadBookmarkSteps(
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

    @Given("^User has previously bookmarked article$")
    fun user_has_previously_bookmarked_article() {
        database.testBookmarkDao().insert(Bookmark(dispatcher.entryResource.first))
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

    @When("^User select to read the bookmarked article$")
    fun user_select_to_read_bookmarked_article() {
        onView(withId(R.id.bookmarked_articles))
            .perform(
                actionOnItemAtPosition<BookmarksAdapter.BookmarkViewHolder>(
                    0,
                    click()
                )
            )
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Bookmarked article should be shown$")
    fun bookmarked_article_should_be_shown() {
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

    private fun expectedUiArticle(): UiArticle {
        val entryJson = getJsonFromResource(dispatcher.entryResource.second)
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
        val entryResource: Pair<String,String> = Pair(
            "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e6c31519:81d950:5d3e1c98",
            "json/feedly_entry1.json"
        )

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.method) {
                "POST" -> {
                    if (checkIfEntryPostRequest(request)) {
                        buildEntryResponse()

                    } else {
                        MockResponse().setResponseCode(404)
                    }
                }

                "GET" -> {
                    if (checkIfEntryGetRequest(request)) {
                        buildEntryResponse()

                    } else {
                        MockResponse().setResponseCode(404)
                    }
                }

                else -> {
                    MockResponse().setResponseCode(404)
                }
            }
        }

        private fun checkIfEntryPostRequest(request: RecordedRequest): Boolean {
            val requestIds = JSONArray(request.body.readUtf8())

            return request.requestUrl.uri().path == "/entries/.mget" &&
                    requestIds.length() == 1 &&
                    requestIds.getJSONObject(0).getString("id") == entryResource.first
        }

        private fun checkIfEntryGetRequest(request: RecordedRequest): Boolean {
            return request.requestUrl.pathSegments().last() == entryResource.first
        }

        private fun buildEntryResponse(): MockResponse {
            val responseJson = JSONArray()
            val entryPath = entryResource.second
            val entryJson = getJsonFromResource(entryPath)
            val entryJsonObject = JSONObject(entryJson)

            responseJson.put(entryJsonObject)

            return MockResponse()
                .setBody(responseJson.toString())
                .setResponseCode(200)
        }
    }

    private data class UiArticle(
        val title: String,
        val author: String,
        val content: String,
        val date: String,
        val imageUrl: String
    )
}