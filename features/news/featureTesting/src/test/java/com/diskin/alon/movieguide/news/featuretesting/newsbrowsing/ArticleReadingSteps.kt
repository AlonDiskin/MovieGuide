package com.diskin.alon.movieguide.news.featuretesting.newsbrowsing

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
import com.diskin.alon.movieguide.news.data.remote.*
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.featuretesting.util.getJsonBodyFromResource
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.controller.MoviesHeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.controller.NewsHeadlinesAdapter.NewsHeadlineViewHolder
import com.diskin.alon.movieguide.news.presentation.model.Article
import com.diskin.alon.movieguide.news.presentation.viewmodel.MoviesHeadlinesViewModelImpl.Companion.PAGE_SIZE
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
import org.robolectric.Shadows

/**
 * Step definitions for 'Article selected for reading' scenario.
 */
class ArticleReadingSteps(server: MockWebServer) : GreenCoffeeSteps() {

    companion object {
        private const val TEST_WEB_FEED_JSON = "json/feed_movie_headlines.json"
        private const val TEST_WEB_ENTRY_JSON = "json/feedly_entry.json"
    }

    private lateinit var movieHeadlinesScenario: FragmentScenario<MoviesHeadlinesFragment>
    private lateinit var articleScenario: ActivityScenario<ArticleActivity>
    private val navController = TestNavHostController(getApplicationContext())

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

        // Prepare mock web server for test scenario
        val dispatcher = object : Dispatcher() {

            override fun dispatch(request: RecordedRequest): MockResponse {
                val supportedEntryPath = "/$FEEDLY_ENTRY_PATH/${parseTestWebEntryResourceId()}"
                val supportedFeedStreamPath = "/$FEEDLY_FEED_PATH"

                return when(request.requestUrl.uri().path) {
                    supportedFeedStreamPath -> {
                        val feedQuery = "$FEEDLY_FEED_ID_PARAM=$MOVIES_NEWS_FEED&" +
                                "$FEEDLY_FEED_SIZE_PARAM=${PAGE_SIZE * 3}"
                        if (request.requestUrl.query() == feedQuery) {
                            MockResponse()
                                .setBody(getJsonBodyFromResource(TEST_WEB_FEED_JSON))
                                .setResponseCode(200)
                        } else{
                            MockResponse().setResponseCode(404)
                        }
                    }

                    supportedEntryPath -> {
                        MockResponse()
                            .setBody(getJsonBodyFromResource(TEST_WEB_ENTRY_JSON))
                            .setResponseCode(200)
                    }

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        server.setDispatcher(dispatcher)
    }

    @Given("^User opened headlines screen$")
    fun userOpenedHeadlinesScreen() {
        // launch movies headlines fragment
        movieHeadlinesScenario = FragmentScenario.launchInContainer(MoviesHeadlinesFragment::class.java)
        // Set test nav controller on headlines fragment
        movieHeadlinesScenario.onFragment { Navigation.setViewNavController(it.requireView(), navController) }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User selects first shown headline$")
    fun userSelectsFirstShownHeadline() {
        onView(withId(R.id.headlines))
            .perform(actionOnItemAtPosition<NewsHeadlineViewHolder>(0, click()))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Article screen should be opened$")
    fun articleScreenShouldBeOpened() {

    }

    @And("^Headline article should be shown$")
    fun headlineArticleShouldBeShown() {
        val testArticle = parseTestWebEntryResourceArticle()
        onView(withId(R.id.title))
            .check(matches(withText(testArticle.title)))

        onView(withId(R.id.published))
            .check(matches(withText(testArticle.date)))

        onView(withId(R.id.author))
            .check(matches(withText(testArticle.author)))

        onView(withId(R.id.content))
            .check(matches(withText(testArticle.content)))
    }

    private fun parseTestWebEntryResourceId(): String {
        val json = getJsonBodyFromResource(TEST_WEB_ENTRY_JSON)
        val jsonArray = JSONArray(json)
        val jsonEntryObject = jsonArray.getJSONObject(0)!!

        return jsonEntryObject.getString("id")
    }

    private fun parseTestWebEntryResourceArticle(): Article {
        val json = getJsonBodyFromResource(TEST_WEB_ENTRY_JSON)
        val jsonArray = JSONArray(json)
        val jsonEntryObject = jsonArray.getJSONObject(0)!!
        val context = getApplicationContext<Context>()!!

        return Article(
            jsonEntryObject.getString("title"),
            jsonEntryObject.getString("author"),
            jsonEntryObject.getJSONObject("summary").getString("content"),
            LocalDateTime(jsonEntryObject.getLong("published")).toString(context.getString(R.string.article_date_format)),
            jsonEntryObject.getJSONObject("visual").getString("url"),
            jsonEntryObject.getString("originId")
        )
    }
}