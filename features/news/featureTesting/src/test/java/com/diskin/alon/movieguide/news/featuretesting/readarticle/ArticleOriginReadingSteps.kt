package com.diskin.alon.movieguide.news.featuretesting.readarticle

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.controller.ArticleFragment
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
import java.net.URLEncoder

/**
 * Step definitions for 'User read article web origin' scenario.
 */
class ArticleOriginReadingSteps(server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private val dispatcher = TestDispatcher()

    init {
        server.setDispatcher(dispatcher)
    }

    @Given("^User opened article in detail screen$")
    fun user_opened_article_in_detail_screen() {
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val bundle = Bundle().apply {
            putString(context.getString(R.string.key_article_id),dispatcher.entryId)
        }
        scenario = launchFragmentInHiltContainer<ArticleFragment>(fragmentArgs = bundle)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^User select read original article$")
    fun user_select_read_original_article() {
        Intents.init()
        onView(withId(R.id.fab))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^App should open web article$")
    fun app_should_open_web_article() {
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))

        val articleUrl = expectedArticleUrl()
        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent

        Truth.assertThat(intent.action).isEqualTo(Intent.ACTION_VIEW)
        Truth.assertThat(intent.data).isEqualTo(Uri.parse(articleUrl))

        Intents.release()
    }

    private fun expectedArticleUrl(): String {
        val json = getJsonFromResource(dispatcher.entryResource)
        val jsonArray = JSONArray(json)
        val jsonObject = jsonArray.getJSONObject(0)

        return jsonObject.getString("originId")
    }

    private class TestDispatcher : Dispatcher() {
        val entryId = "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e6c31519:81d950:5d3e1c98uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e6c31519:81d950:5d3e1c98"
        val entryResource = "json/feedly_entry.json"
        private val entryPath = "/entries/".plus(
            URLEncoder.encode(
                entryId,
                "UTF-8"
            )
        )

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.path) {
                entryPath -> MockResponse()
                    .setBody(getJsonFromResource(entryResource))
                    .setResponseCode(200)

                else -> MockResponse().setResponseCode(404)
            }
        }
    }
}