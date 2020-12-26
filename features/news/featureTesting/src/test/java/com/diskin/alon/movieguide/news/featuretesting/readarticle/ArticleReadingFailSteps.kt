package com.diskin.alon.movieguide.news.featuretesting.readarticle

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.news.data.remote.FEEDLY_ENTRY_PATH
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.data.Article
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler.Companion.ERR_API_SERVER
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler.Companion.ERR_DEVICE_NETWORK
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.*
import org.hamcrest.CoreMatchers.allOf
import org.joda.time.LocalDateTime
import org.json.JSONArray
import org.robolectric.Shadows

/**
 * Step definitions for 'Article reading error handling' scenario.
 */
class ArticleReadingFailSteps(private val server: MockWebServer) : GreenCoffeeSteps() {

    companion object {
        private const val TEST_WEB_ENTRY_JSON = "json/feedly_entry.json"
    }

    private lateinit var scenario: ActivityScenario<ArticleActivity>
    private lateinit var expectedErrorMessage: String

    @Given("^Existing app error due to \"([^\"]*)\"$")
    fun existingAppErrorDueTo(errorCause: String) {
        when(errorCause) {
            "device networking" -> {
                expectedErrorMessage = ERR_DEVICE_NETWORK
                val dispatcher = object : Dispatcher() {

                    override fun dispatch(request: RecordedRequest): MockResponse {
                        return MockResponse()
                            .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
                    }
                }

                server.setDispatcher(dispatcher)
            }

            "remote server" -> {
                expectedErrorMessage = ERR_API_SERVER
                val dispatcher = object : Dispatcher() {
                    override fun dispatch(request: RecordedRequest): MockResponse {
                        val apiEntryPath = "/$FEEDLY_ENTRY_PATH/${getTestWebEntryId()}"
                        return when (request.requestUrl.uri().path) {
                            apiEntryPath -> MockResponse().setResponseCode(500)

                            else -> throw IllegalArgumentException("unexpected request")
                        }
                    }
                }

                server.setDispatcher(dispatcher)
            }

            else -> throw IllegalArgumentException("unknown scenario arg")
        }
    }

    @When("^User opened article screen$")
    fun userOpenedArticleScreen() {
        // Launch article activity with article id matching test web article
        val context = getApplicationContext<Context>()!!
        val keyArticleId = context.getString(R.string.key_article_id)
        val articleIdArg = getTestWebEntryId()

        Intent(context, ArticleActivity::class.java)
            .apply { putExtra(keyArticleId, articleIdArg) }
            .also { scenario = ActivityScenario.launch(it) }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^User should be notifies with message describing error$")
    fun userShouldBeNotifiesWithMessageDescribingError() {
        // Verify snack bar is displayed with expected error message
        onView(withId(R.id.snackbar_text))
            .check(
                matches(
                    allOf(
                        withText(expectedErrorMessage),
                        isDisplayed()
                    )
                )
            )
    }

    @And("^Should \"([^\"]*)\" retry option$")
    fun shouldRetryOption(arg: String) {
        when(arg) {
            "provide" -> {
                onView(withId(R.id.snackbar_action))
                    .check(
                        matches(
                            allOf(
                                withText(R.string.action_retry),
                                isDisplayed()
                            )
                        )
                    )
            }

            "not provide" -> {
                onView(withId(R.id.snackbar_action))
                    .check(
                        matches(
                            allOf(
                                withText(R.string.action_retry),
                                withEffectiveVisibility(Visibility.GONE)
                            )
                        )
                    )
            }

            else -> throw IllegalArgumentException("unknown scenario arg")
        }
    }

    @When("^Error \"([^\"]*)\" is resolved$")
    fun errorIsResolved(errorCause: String) {
        when(errorCause) {
            "device networking",
            "remote server" -> {
                val dispatcher = object : Dispatcher() {
                    override fun dispatch(request: RecordedRequest): MockResponse {
                        val apiEntryPath = "/$FEEDLY_ENTRY_PATH/${getTestWebEntryId()}"
                        return when (request.requestUrl.uri().path) {
                            apiEntryPath -> MockResponse()
                                .setResponseCode(200)
                                .setBody(getJsonFromResource(TEST_WEB_ENTRY_JSON))

                            else -> throw IllegalArgumentException("unexpected request")
                        }
                    }
                }

                server.setDispatcher(dispatcher)
            }

            else -> throw IllegalArgumentException("unknown scenario arg")
        }
    }

    @And("^User \"([^\"]*)\" retry$")
    fun userRetry(retry: String) {
        when(retry) {
            "select" -> {
                // Click on retry button
                onView(withId(R.id.snackbar_action))
                    .perform(click())

                Shadows.shadowOf(Looper.getMainLooper()).idle()
            }

            "not select" -> {
                // Do not do anything
            }

            else -> throw IllegalArgumentException("unknown scenario arg")
        }
    }

    @Then("^Article screen should \"([^\"]*)\" article$")
    fun articleScreenShouldDisplayArticle(display: String) {
        val testArticle = parseTestWebEntryResourceArticle()
        when(display) {
            "display" -> {
                // Verify web article is shown in ui
                onView(withId(R.id.title))
                    .check(matches(withText(testArticle.title)))

                onView(withId(R.id.published))
                    .check(matches(withText(testArticle.date)))

                onView(withId(R.id.author))
                    .check(matches(withText(testArticle.author)))

                onView(withId(R.id.content))
                    .check(matches(withText(testArticle.content)))
            }

            "not display" -> {
                // Verify article ui do not show any info
                onView(withId(R.id.title))
                    .check(matches(withText("")))

                onView(withId(R.id.published))
                    .check(matches(withText("")))

                onView(withId(R.id.author))
                    .check(matches(withText("")))

                onView(withId(R.id.content))
                    .check(matches(withText("")))
            }

            else -> throw IllegalArgumentException("unknown scenario arg")
        }
    }

    private fun getTestWebEntryId(): String {
        val json = getJsonFromResource(TEST_WEB_ENTRY_JSON)
        val jsonResponseArray = JSONArray(json)
        val jsonEntry = jsonResponseArray.getJSONObject(0)!!

        return jsonEntry.getString("id")
    }

    private fun parseTestWebEntryResourceArticle(): Article {
        val json = getJsonFromResource(TEST_WEB_ENTRY_JSON)
        val jsonArray = JSONArray(json)
        val jsonEntryObject = jsonArray.getJSONObject(0)!!
        val context = getApplicationContext<Context>()!!

        return Article(
            jsonEntryObject.getString("title"),
            jsonEntryObject.getString("author"),
            jsonEntryObject.getJSONObject("summary").getString("content"),
            LocalDateTime(jsonEntryObject.getLong("published")).toString(context.getString(R.string.article_date_format)),
            jsonEntryObject.getJSONObject("visual").getString("url"),
            jsonEntryObject.getString("originId"),false
        )
    }
}