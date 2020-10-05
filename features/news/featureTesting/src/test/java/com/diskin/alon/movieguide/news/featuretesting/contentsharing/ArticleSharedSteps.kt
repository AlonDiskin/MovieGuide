package com.diskin.alon.movieguide.news.featuretesting.contentsharing

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
import com.diskin.alon.movieguide.news.data.remote.FEEDLY_ENTRY_PATH
import com.diskin.alon.movieguide.news.featuretesting.util.getJsonBodyFromResource
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModelImpl.Companion.KEY_ARTICLE_ID
import com.google.common.truth.Truth
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
 * Step definitions for 'Movie news article shared' scenario.
 */
class ArticleSharedSteps(server: MockWebServer) : GreenCoffeeSteps() {

    companion object {
        const val TEST_WEB_JSON = "json/feedly_entry.json"
    }

    private lateinit var scenario: ActivityScenario<ArticleActivity>

    init {
        // Prepare mock web server for test scenario
        val dispatcher = object : Dispatcher() {

            override fun dispatch(request: RecordedRequest): MockResponse {
                val entryResourceId = parseTestWebEntryResourceId()
                val validEntryId = entryResourceId.replace("[/+]".toRegex(),"")
                val supportedPath = "/$FEEDLY_ENTRY_PATH/$validEntryId"
                val requestPath = request.requestUrl.url().path

                return when(requestPath) {
                    supportedPath -> {
                        MockResponse()
                            .setBody(getJsonBodyFromResource(TEST_WEB_JSON))
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
        // Launch article activity
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val intent = Intent(context, ArticleActivity::class.java).apply {
            putExtra(KEY_ARTICLE_ID,parseTestWebEntryResourceId())
        }
        scenario = ActivityScenario.launch(intent)
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

        Truth.assertThat(intent.type).isEqualTo(context.getString(R.string.mime_type_text))
        Truth.assertThat(intent.getStringExtra(Intent.EXTRA_TEXT))
            .isEqualTo(parseTestWebEntryResourceArticleUrl())

        Intents.release()
    }

    private fun parseTestWebEntryResourceId(): String {
        val json = getJsonBodyFromResource(TEST_WEB_JSON)
        val jsonArray = JSONArray(json)
        val jsonEntryObject = jsonArray.getJSONObject(0)!!

        return jsonEntryObject.getString("id")
    }

    private fun parseTestWebEntryResourceArticleUrl(): String {
        val json = getJsonBodyFromResource(TEST_WEB_JSON)
        val jsonArray = JSONArray(json)
        val jsonEntryObject = jsonArray.getJSONObject(0)!!

        return jsonEntryObject.getString("originId")
    }
}