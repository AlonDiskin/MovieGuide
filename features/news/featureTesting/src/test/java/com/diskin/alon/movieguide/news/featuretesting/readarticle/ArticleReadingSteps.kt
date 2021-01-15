package com.diskin.alon.movieguide.news.featuretesting.readarticle

import android.content.Context
import android.content.Intent
import android.os.Looper
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
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesAdapter.HeadlineViewHolder
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.viewmodel.HeadlinesViewModelImpl.Companion.PAGE_SIZE
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.joda.time.LocalDateTime
import org.json.JSONArray
import org.robolectric.Shadows
import java.net.URLEncoder

/**
 * Step definitions for 'Article selected for reading' scenario.
 */
class ArticleReadingSteps(server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var movieHeadlinesScenario: ActivityScenario<HiltTestActivity>
    private lateinit var articleScenario: ActivityScenario<ArticleActivity>
    private val navController = TestNavHostController(getApplicationContext())
    private val dispatcher = TestDispatcher()

    init {
        // Setup nav controller
        navController.setGraph(R.navigation.news_nav_graph)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.articleActivity) {
                val context = getApplicationContext<Context>()!!
                val keyArticleId = context.getString(R.string.key_article_id)
                val articleIdArg = navController
                    .currentBackStackEntry!!.arguments!!.getString(keyArticleId)!!
                val intent = Intent(context,ArticleActivity::class.java)
                    .apply { putExtra(keyArticleId,articleIdArg) }

                // Launch article detail activity when user navigates to it from fragment
                // (manually,robolectric bug)
                articleScenario = ActivityScenario.launch(intent)

                // Wait for main looper to idle
                Shadows.shadowOf(Looper.getMainLooper()).idle()
            }
        }

        // Prepare test web server
        server.setDispatcher(dispatcher)
    }

    @Given("^User opened articles screen$")
    fun userOpenedArticlesScreen() {
        // launch movies headlines fragment
        movieHeadlinesScenario = launchFragmentInHiltContainer<HeadlinesFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        // Set test nav controller on headlines fragment
        movieHeadlinesScenario.onActivity {
            Navigation.setViewNavController(
                it.supportFragmentManager.fragments[0].requireView(),
                navController
            )
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User selects first shown article$")
    fun userSelectsFirstShownArticle() {
        onView(withId(R.id.headlines))
            .perform(actionOnItemAtPosition<HeadlineViewHolder>(0, click()))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @And("^Article should be shown$")
    fun articleShouldBeShown() {
        val article = expectedUiArticle()
        onView(withId(R.id.title))
            .check(matches(withText(article.title)))

        onView(withId(R.id.published))
            .check(matches(withText(article.date)))

        onView(withId(R.id.author))
            .check(matches(withText(article.author)))

        onView(withId(R.id.content))
            .check(matches(withText(article.content)))
    }

    private fun expectedUiArticle(): UiArticle {
        val json = getJsonFromResource(dispatcher.entryResource)
        val jsonArray = JSONArray(json)
        val jsonObject = jsonArray.getJSONObject(0)
        val context = getApplicationContext<Context>()!!

        return UiArticle(
            jsonObject.getString("title"),
            LocalDateTime(jsonObject.getLong("published")).toString(context.getString(R.string.article_date_format)),
            jsonObject.getString("author"),
            jsonObject.getJSONObject("summary").getString("content")
        )
    }

    private class TestDispatcher : Dispatcher() {
        val feedResource = "json/feed_movie_headlines.json"
        val entryResource = "json/feedly_entry.json"
        private val feedPath = "/streams/contents?streamId=".plus(
            URLEncoder.encode(
                "feed/http://www.collider.com/rss.asp",
                "UTF-8"
            )
        ).plus(
            "&count=${PAGE_SIZE * 3}"
        )

        private val entryPath = "/entries/".plus(
            URLEncoder.encode(
                "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e6c31519:81d950:5d3e1c98uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e6c31519:81d950:5d3e1c98",
                "UTF-8"
            )
        )

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.path) {
                feedPath -> MockResponse()
                    .setBody(getJsonFromResource(feedResource))
                    .setResponseCode(200)

                entryPath -> MockResponse()
                    .setBody(getJsonFromResource(entryResource))
                    .setResponseCode(200)

                else -> MockResponse().setResponseCode(404)
            }
        }

    }

    private data class UiArticle(
        val title: String,
        val date: String,
        val author: String,
        val content: String)
}