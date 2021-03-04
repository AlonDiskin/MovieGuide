package com.diskin.alon.movieguide.news.featuretesting.newsupdatenotification

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.ActivityManager.getMyMemoryState
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.work.ListenableWorker.Result
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.common.util.messaging.NewsNotificationConfigEvent
import com.diskin.alon.movieguide.news.featuretesting.R
import com.diskin.alon.movieguide.news.featuretesting.newsupdatenotification.UserNotifiedStepsRunner.TestWorkerFactory
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationConfigListenerService
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationSchedulerImpl
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationWorker
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
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
import org.json.JSONObject
import org.robolectric.Robolectric
import java.net.URLEncoder

/**
 * Step definitions for 'Show notification in user device' scenario.
 */
class UserNotifiedSteps(
    server: MockWebServer,
    private val workerFactory: TestWorkerFactory
) : GreenCoffeeSteps() {

    private val appContext = getApplicationContext<Context>()
    private val dispatcher = TestDispatcher()

    init {
        // Prepare test web server
        server.setDispatcher(dispatcher)

        // Init listener service
        Robolectric.setupService(NewsNotificationConfigListenerService::class.java)
    }

    @Given("^Unread by user articles are published$")
    fun unread_by_user_articles_are_published() {
        // Set last read article date by user is older then most recent published remote article
        val sp = PreferenceManager.getDefaultSharedPreferences(appContext)
        val key = appContext.getString(R.string.key_recent_article_read)
        val recentRemotePublished = getRecentRemotePublishedArticleDate()

        sp.edit()
            .putLong(key, recentRemotePublished - 1000000L)
            .commit()
    }

    @And("^News notification is disabled$")
    fun news_notification_is_disabled() {
        // Verify notification worker is not enqueued
        val workInfos = WorkManager.getInstance(appContext)
            .getWorkInfosForUniqueWork(NewsNotificationSchedulerImpl.NEWS_NOTIFICATION_WORK_NAME)
            .get()

        assertThat(workInfos.size).isEqualTo(0)
    }

    @When("^User enable news update notification$")
    fun user_enable_news_update_notification() {
        // Publish config event that enables news notification
        EventBus.getDefault().post(
            NewsNotificationConfigEvent(enabled = true, vibrate = true)
        )
    }

    @And("^App currently usage is \"([^\"]*)\"$")
    fun app_currently_usage(usageArg: String) {
        val processInfo = slot<RunningAppProcessInfo>()
        mockkStatic(ActivityManager::class)

        when(usageArg) {
            "in foreground" -> every { getMyMemoryState(capture(processInfo)) } answers {
                processInfo.captured.importance = RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }

            "not in foreground" -> every { getMyMemoryState(capture(processInfo)) } answers {
                processInfo.captured.importance = RunningAppProcessInfo.IMPORTANCE_GONE
            }

            else -> throw IllegalArgumentException("Unknown scenario arg:${usageArg}")
        }
    }

    @Then("^App should \"([^\"]*)\" user upon notification service run$")
    fun app_should_notify_user_upon_notification_service_run(notifyArg: String) {
        val workManager = WorkManager.getInstance(appContext)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(appContext)!!
        val notificationManager: NotificationManager = appContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val workInfo = workManager.getWorkInfosForUniqueWork(
            NewsNotificationSchedulerImpl.NEWS_NOTIFICATION_WORK_NAME).get()[0]

        // Verify notification work has been scheduled
        assertThat(workInfo.state).isEqualTo(WorkInfo.State.ENQUEUED)

        // Set notification work constrains and delay as met, for work to run
        testDriver.setPeriodDelayMet(workInfo.id)
        testDriver.setAllConstraintsMet(workInfo.id)

        // TestDriver currently do not supporting executing work for RxWorker, so we
        // need to do this manually
        val testObserver = workerFactory.worker.createWork().test()

        // Verify worker returned  successful result
        testObserver.assertValue(Result.success())

        when(notifyArg) {
            "notify" -> {
                // Verify notification has been shown
                val channel = NotificationManagerCompat.from(appContext)
                    .getNotificationChannel(NewsNotificationWorker.CHANNEL_ID)
                assertThat(channel).isNotNull()
                assertThat(notificationManager.activeNotifications.size).isEqualTo(1)
            }

            "not notify" -> {
                // Verify notification has not been shown
                val channel = NotificationManagerCompat.from(appContext)
                    .getNotificationChannel(NewsNotificationWorker.CHANNEL_ID)
                assertThat(channel).isNull()
                assertThat(notificationManager.activeNotifications.size).isEqualTo(0)
            }

            else -> throw IllegalArgumentException("Unknown scenario arg:${notifyArg}")
        }
    }

    private fun getRecentRemotePublishedArticleDate(): Long {
        val json = getJsonFromResource(dispatcher.recentEntryFeedResource)
        val jsonResponse = JSONObject(json)
        val jsonArray = jsonResponse.getJSONArray("items")
        val jsonObject = jsonArray.getJSONObject(0)

        return jsonObject.getLong("published")
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