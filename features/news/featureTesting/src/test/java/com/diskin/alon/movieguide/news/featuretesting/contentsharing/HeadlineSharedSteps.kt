package com.diskin.alon.movieguide.news.featuretesting.contentsharing

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.news.data.remote.FEEDLY_FEED_ID_PARAM
import com.diskin.alon.movieguide.news.data.remote.FEEDLY_FEED_PATH
import com.diskin.alon.movieguide.news.data.remote.FEEDLY_FEED_SIZE_PARAM
import com.diskin.alon.movieguide.news.data.remote.MOVIES_NEWS_FEED
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.featuretesting.util.getJsonBodyFromResource
import com.diskin.alon.movieguide.news.featuretesting.util.parseFeedlyResponseJsonToNewsHeadlines
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.viewmodel.HeadlinesViewModelImpl.Companion.PAGE_SIZE
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesAdapter.HeadlineViewHolder
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.robolectric.Shadows

/**
 * Step definitions for 'Movie news headline shared' scenario.
 */
class HeadlineSharedSteps(server: MockWebServer) : GreenCoffeeSteps() {

    companion object {
        const val TEST_WEB_JSON = "json/feed_movie_headlines.json"
    }

    private lateinit var scenario: FragmentScenario<HeadlinesFragment>
    private val expectedUiHeadlines =
        parseFeedlyResponseJsonToNewsHeadlines(getJsonBodyFromResource(TEST_WEB_JSON))

    init {
        // Prepare mock web server for test scenario
        val dispatcher = object : Dispatcher() {

            override fun dispatch(request: RecordedRequest): MockResponse {
                val requestPath = "${request.requestUrl.url().path}?${request.requestUrl.query()}"
                val supportedPath = "/$FEEDLY_FEED_PATH?" +
                        "$FEEDLY_FEED_ID_PARAM=$MOVIES_NEWS_FEED&" +
                        "$FEEDLY_FEED_SIZE_PARAM=${PAGE_SIZE * 3}"

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

    @Given("^User opened news headline screen$")
    fun userOpenedNewsHeadlineScreen() {
        scenario = FragmentScenario.launchInContainer(HeadlinesFragment::class.java)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^All headlines should provide sharing option$")
    fun allHeadlinesShouldProvideSharingOption() {
        // Verify all test headlines loaded to ui
        scenario.onFragment { fragment ->
            val recycler = fragment.view!!.findViewById<RecyclerView>(R.id.headlines)

            assertThat(recycler.adapter!!.itemCount).isEqualTo(expectedUiHeadlines.size)
        }

        // Verify each displayed headline show sharing option
        expectedUiHeadlines.forEachIndexed { index, _ ->
            // Scroll to expected headline layout position
            onView(withId(R.id.headlines))
                .perform(scrollToPosition<HeadlineViewHolder>(index))

            Shadows.shadowOf(Looper.getMainLooper()).idle()

            // Check headline at this position provide ui sharing option
            onView(withRecyclerView(R.id.headlines).atPosition(index))
                .check(matches(
                    hasDescendant(
                        withId(R.id.shareButton)
                    )
                ))
        }
    }

    @When("^User select to share the first headline$")
    fun userSelectToShareTheFirstHeadline() {
        // Scroll to first headline
        onView(withId(R.id.headlines))
            .perform(scrollToPosition<HeadlineViewHolder>(0))

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Init intents capturing before click operation
        Intents.init()

        // Share first headline by clicking on share button
        onView(withRecyclerView(R.id.headlines).atPositionOnView(0, R.id.shareButton))
            .perform(click())

        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^App should open device share menu$")
    fun appShouldOpenDeviceShareMenu() {

        // Then fragment should share headline article url via Android Sharesheet
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent
        val context = ApplicationProvider.getApplicationContext<Context>()!!

        assertThat(intent.type).isEqualTo(context.getString(com.diskin.alon.movieguide.news.presentation.R.string.mime_type_text))
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT))
            .isEqualTo(expectedUiHeadlines.first().articleUrl)

        Intents.release()
    }
}