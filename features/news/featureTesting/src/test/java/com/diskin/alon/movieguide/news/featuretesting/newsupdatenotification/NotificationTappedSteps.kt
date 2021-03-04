package com.diskin.alon.movieguide.news.featuretesting.newsupdatenotification

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.ActivityManager.getMyMemoryState
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.util.messaging.NewsNotificationConfigEvent
import com.diskin.alon.movieguide.news.featuretesting.newsupdatenotification.NotificationTappedStepsRunner.TestWorkerFactory
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationConfigListenerService
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationSchedulerImpl
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationWorker
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.greenrobot.eventbus.EventBus
import org.robolectric.Robolectric
import java.net.URLEncoder

/**
 * Step definitions for 'News screen opened when notification tapped' scenario.
 */
class NotificationTappedSteps(
    server: MockWebServer,
    private val workerFactory: TestWorkerFactory
) : GreenCoffeeSteps() {

    private val dispatcher = TestDispatcher()
    private val notificationManager: NotificationManager = getApplicationContext<Context>()
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        // Prepare test web server
        server.setDispatcher(dispatcher)

        // Init listener service
        Robolectric.setupService(NewsNotificationConfigListenerService::class.java)
    }

    @Given("^News notification is shown$")
    fun news_notification_is_shown() {
        // Publish config event that enables news notification, and register work request
        EventBus.getDefault().post(
            NewsNotificationConfigEvent(enabled = true, vibrate = true)
        )

        // Next steps config work request to run work in manner that shows notification

        // Set app process as not foreground one, in order for notification to be shown by worker
        val processInfo = slot<RunningAppProcessInfo>()
        mockkStatic(ActivityManager::class)
        every { getMyMemoryState(capture(processInfo)) } answers {
            processInfo.captured.importance = RunningAppProcessInfo.IMPORTANCE_GONE
        }

        // Config work to execute
        val workManager = WorkManager.getInstance(getApplicationContext())
        val testDriver = WorkManagerTestInitHelper.getTestDriver(getApplicationContext())!!
        val workInfo = workManager.getWorkInfosForUniqueWork(
            NewsNotificationSchedulerImpl.NEWS_NOTIFICATION_WORK_NAME).get()[0]

        // Set notification work constrains and delay as met, for work to run
        testDriver.setPeriodDelayMet(workInfo.id)
        testDriver.setAllConstraintsMet(workInfo.id)

        // TestDriver currently do not supporting executing work for RxWorker, so we
        // need to do this manually
        workerFactory.worker.createWork().test()

        // Verify notification is indeed shown
        assertThat(notificationManager.activeNotifications.size).isEqualTo(1)
        assertThat(notificationManager.activeNotifications[0].id)
            .isEqualTo(NewsNotificationWorker.NOTIFICATION_ID)
    }

    @When("^User tap on notification$")
    fun user_tap_on_notification() {
        // Currently there is no way to simulate the sending of pending intent, so
        // verification for action would be done in next step against data of pending intent itself
    }

    @Then("^App should launch and open news screen$")
    fun app_should_launch_and_open_news_screen() {
        // Verify sent intent configured to open app main activity
        val notifications = notificationManager.activeNotifications
        assertThat(notifications[0].notification.contentIntent.creatorPackage)
            .isEqualTo(getApplicationContext<Context>().packageName)
    }

    private class TestDispatcher : Dispatcher() {
        val recentEntryFeedResource = "json/recent_published_entry_feed.json"
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
                    .setBody(getJsonFromResource(recentEntryFeedResource))
                    .setResponseCode(200)

                else -> MockResponse().setResponseCode(404)
            }
        }
    }
}