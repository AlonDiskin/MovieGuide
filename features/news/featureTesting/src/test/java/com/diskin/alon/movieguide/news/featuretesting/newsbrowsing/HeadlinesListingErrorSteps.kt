package com.diskin.alon.movieguide.news.featuretesting.newsbrowsing

import android.os.Looper
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.diskin.alon.movieguide.news.data.remote.FEEDLY_FEED_PATH
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler.Companion.ERR_API_SERVER
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler.Companion.ERR_DEVICE_NETWORK
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.allOf
import org.robolectric.Shadows

/**
 * Step definitions for 'Headlines listing fail' scenario.
 */
class HeadlinesListingErrorSteps(private val server: MockWebServer) : GreenCoffeeSteps(){

    private lateinit var scenario: FragmentScenario<HeadlinesFragment>
    private lateinit var expectedErrorMessage: String

    @Given("^Existing app error due to \"([^\"]*)\"$")
    fun existingAppErrorDueTo(error: String) {
        // Create app error fixture
        when(error) {
            "device networking" -> {
                expectedErrorMessage = ERR_DEVICE_NETWORK
                server.shutdown()
            }

            "remote server" -> {
                expectedErrorMessage = ERR_API_SERVER
                val dispatcher = object : Dispatcher() {
                    override fun dispatch(request: RecordedRequest): MockResponse {
                        val apiPath = "/$FEEDLY_FEED_PATH"
                        return when (request.requestUrl.url().path) {
                            apiPath -> MockResponse().setResponseCode(500)

                            else -> throw IllegalArgumentException("unexpected request")
                        }
                    }
                }

                server.setDispatcher(dispatcher)
            }
        }
    }

    @When("^User open news headlines screen$")
    fun userOpenNewsHeadlinesScreen() {
        // Launch movies headlines fragment
        scenario = FragmentScenario.launchInContainer(
            HeadlinesFragment::class.java,
            null,
            R.style.AppTheme,
            null
        )
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^User should be notifies with message describing error$")
    fun userShouldBeNotifiesWithMessageDescribingError() {
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

    @And("^Should be provided with retry option$")
    fun shouldBeProvidedWithRetryOption() {
        onView(withId(R.id.snackbar_action))
            .check(matches(
                allOf(
                    withText(R.string.action_retry),
                    isDisplayed()
                )
            ))
    }
}