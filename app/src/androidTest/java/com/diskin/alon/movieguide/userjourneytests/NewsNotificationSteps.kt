package com.diskin.alon.movieguide.userjourneytests

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.uiautomator.UiSelector
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.diskin.alon.movieguide.R
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationConfigListenerService
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationSchedulerImpl
import com.diskin.alon.movieguide.userjourneytests.NewsNotificationStepsRunner.TestWorkerFactory
import com.diskin.alon.movieguide.util.DeviceUtil
import com.diskin.alon.movieguide.util.FileUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.net.URLEncoder

/**
 * Step definitions for 'User notified for unread available articles' scenario.
 */
class NewsNotificationSteps(
    server: MockWebServer,
    private val workerFactory: TestWorkerFactory
) : GreenCoffeeSteps() {

    init {
        // Prepare test server
        server.setDispatcher(TestDispatcher())

        // Start notification config listener service
        getApplicationContext<Context>().startService(
            Intent(getApplicationContext(),NewsNotificationConfigListenerService::class.java)
        )
    }

    @Given("^User launched app from device home$")
    fun user_launched_app_from_device_home() {
        // Launch app from device home screen
        DeviceUtil.openDeviceHome()
        DeviceUtil.launchApp()
    }

    @And("^Enabled news update notification service$")
    fun enabled_news_update_notification_service() {
        // Open settings screen
        onView(withId(R.id.settings))
            .perform(click())

        // Enable news notification
        onView(withText(R.string.pref_news_notification_title))
            .perform(click())
    }

    @And("^User leave app$")
    fun user_leave_app() {
        // Leave app and go back to home screen
        DeviceUtil.pressBack()
        DeviceUtil.pressBack()
    }

    @When("^New unread by user articles are published$")
    fun new_unread_by_user_articles_are_published() {
        // Set notification worker constrains as met, and run worker
        val workManager = WorkManager.getInstance(getApplicationContext())
        val testDriver = WorkManagerTestInitHelper.getTestDriver(getApplicationContext())!!
        val workInfo = workManager.getWorkInfosForUniqueWork(
            NewsNotificationSchedulerImpl.NEWS_NOTIFICATION_WORK_NAME).get()[0]

        testDriver.setPeriodDelayMet(workInfo.id)
        testDriver.setAllConstraintsMet(workInfo.id)
        workerFactory.worker.createWork().test()
    }

    @Then("^App should show a status bar notification$")
    fun app_should_show_a_status_bar_notification() {
        // Verify notification is shown

        // Next step verifies notification displayed
    }

    @When("^User open notification and tap it$")
    fun user_open_notification_and_tap_it() {
        // Tap on notification
        val notificationText = getApplicationContext<Context>()
            .getString(R.string.notification_title)
        val device = DeviceUtil.getDevice()

        device.openNotification()
        device.findObject(UiSelector().text(notificationText)).click()

    }

    @Then("^App should launch with news screen open$")
    fun app_should_launch_with_news_screen_open() {
        // Verify news fragment is shown
        onView(withId(R.id.headlines))
            .check(matches(isDisplayed()))

        DeviceUtil.pressBack()
    }

    private class TestDispatcher : Dispatcher() {
        val recentEntryFeedResource = "assets/json/recent_published_entry_feed.json"
        private val recentEntryPath = "/streams/contents?streamId=".plus(
            URLEncoder.encode(
                "feed/http://www.collider.com/rss.asp",
                "UTF-8"
            )
        ).plus(
            "&count=1"
        )

        override fun dispatch(request: RecordedRequest): MockResponse {
            return when(request.path) {
                recentEntryPath -> MockResponse()
                    .setBody(FileUtil.readStringFromFile(recentEntryFeedResource))
                    .setResponseCode(200)

                else -> MockResponse().setResponseCode(404)
            }
        }
    }
}