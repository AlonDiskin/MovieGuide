package com.diskin.alon.movieguide.news.featuretesting.sharebookmark

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import com.diskin.alon.movieguide.news.featuretesting.di.TestDatabase
import com.diskin.alon.movieguide.news.presentation.R.id
import com.diskin.alon.movieguide.news.presentation.R.string
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

/**
 * Step definitions for 'User share bookmark' scenario.
 */
class ShareBookmarkSteps(
    server: MockWebServer,
    private val database: TestDatabase
) : GreenCoffeeSteps() {

    private lateinit var bookmarksFragmentScenario: ActivityScenario<HiltTestActivity>
    private val dispatcher = TestDispatcher()

    init {
        // Prepare test server
        server.setDispatcher(dispatcher)
    }

    @Given("^User has previously bookmarked article$")
    fun user_has_previously_bookmarked_article() {
        database.testBookmarkDao().insert(Bookmark(dispatcher.entryResource.first))
    }

    @And("^User open bookmarks screen$")
    fun user_open_bookmarks_screen() {
        bookmarksFragmentScenario = launchFragmentInHiltContainer<BookmarksFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^Select to share bookmark$")
    fun select_to_share_bookmark() {
        Intents.init()

        onView(withId(id.bookmarkOptions))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText(string.title_action_share_bookmark))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^Bookmark article link should be shared$")
    fun bookmark_article_link_should_be_shared() {
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent
        val context = ApplicationProvider.getApplicationContext<Context>()!!

        assertThat(intent.type).isEqualTo(context.getString(string.mime_type_text))
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT))
            .isEqualTo(expectedSharedArticleUrl())

        Intents.release()
    }

    private fun expectedSharedArticleUrl(): String {
        val entryJson = getJsonFromResource(dispatcher.entryResource.second)
        val entryJsonObject = JSONObject(entryJson)

        return entryJsonObject.getString("originId")
    }

    private class TestDispatcher: Dispatcher() {
        val entryResource: Pair<String,String> = Pair(
            "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e6c31519:81d950:5d3e1c98",
            "json/feedly_entry1.json"
        )

        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = "/entries/.mget"

            return when(request.requestUrl.uri().path) {
                path -> {
                    if (checkIfEntryRequest(request)) {
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

        private fun checkIfEntryRequest(request: RecordedRequest): Boolean {
            val requestIds = JSONArray(request.body.readUtf8())

            return requestIds.length() == 1 &&
                    requestIds.getJSONObject(0).getString("id") == entryResource.first
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
}