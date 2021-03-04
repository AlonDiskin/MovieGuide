package com.diskin.alon.movieguide.news.featuretesting.sharearticle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.news.data.remote.FEEDLY_ENTRY_PATH
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.controller.ArticleFragment
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray
import org.robolectric.Shadows

/**
 * Step definitions for 'News article shared' scenario.
 */
class ShareArticleSteps(server: MockWebServer) : GreenCoffeeSteps() {

    companion object {
        const val TEST_WEB_JSON = "json/feedly_entry.json"
    }

    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    init {
        // Prepare mock web server for test scenario
        val dispatcher = object : Dispatcher() {

            override fun dispatch(request: RecordedRequest): MockResponse {
                val supportedPath = "/$FEEDLY_ENTRY_PATH/${parseTestWebEntryResourceId()}"

                return when(request.requestUrl.uri().path) {
                    supportedPath -> {
                        MockResponse()
                            .setBody(getJsonFromResource(TEST_WEB_JSON))
                            .setResponseCode(200)
                    }

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        server.setDispatcher(dispatcher)
    }

    @Given("^User opened news article screen$")
    fun userOpenedNewsArticleScreen() {
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val bundle = Bundle().apply {
            putString(context.getString(R.string.key_article_id),parseTestWebEntryResourceId())
        }
        scenario = launchFragmentInHiltContainer<ArticleFragment>(fragmentArgs = bundle)
    }

    @When("^User select to share the article$")
    fun userSelectToShareTheArticle() {
        Intents.init()

        onView(withId(R.id.action_share))
            .perform(click())

        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^App should open device share menu$")
    fun appShouldOpenDeviceShareMenu() {
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent
        val context = ApplicationProvider.getApplicationContext<Context>()!!

        assertThat(intent.type).isEqualTo(context.getString(R.string.mime_type_text))
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT))
            .isEqualTo(parseTestWebEntryResourceArticleUrl())

        Intents.release()
    }

    private fun parseTestWebEntryResourceId(): String {
        val json = getJsonFromResource(TEST_WEB_JSON)
        val jsonArray = JSONArray(json)
        val jsonEntryObject = jsonArray.getJSONObject(0)!!

        return jsonEntryObject.getString("id")
    }

    private fun parseTestWebEntryResourceArticleUrl(): String {
        val json = getJsonFromResource(TEST_WEB_JSON)
        val jsonArray = JSONArray(json)
        val jsonEntryObject = jsonArray.getJSONObject(0)!!

        return jsonEntryObject.getString("originId")
    }
}