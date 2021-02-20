package com.diskin.alon.movieguide.news.infrastructure

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.ActivityManager.getMyMemoryState
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import org.joda.time.LocalDateTime
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

/**
 * [NewsNotificationWorker] unit unit class.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class NewsNotificationWorkerTest {

    // Test subject
    private lateinit var worker: NewsNotificationWorker

    // Collaborators
    private val remoteDateProvider: RemoteRecentDateProvider = mockk()
    private val localDateProvider: LocalRecentDateProvider = mockk()
    private val workerFactory = object : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return NewsNotificationWorker(
                appContext,
                workerParameters,
                remoteDateProvider,
                localDateProvider
            )
        }
    }

    @Before
    fun setUp() {
        // Init subject
        worker = TestListenableWorkerBuilder<NewsNotificationWorker>(
            context = getApplicationContext()
        )
            .setWorkerFactory(workerFactory)
            .build()
    }

    @Test
    fun showNotificationWhenUnreadArticlesExistAndAppNotInForeground() {
        // Test case fixture
        val remoteRecentArticleDate = LocalDateTime(2020,2,12,12,15).toDate()
        val userLastReadArticleDate = LocalDateTime(2020,2,11,12,15).toDate()
        val processInfo = slot<RunningAppProcessInfo>()

        every { remoteDateProvider.getDate() } returns Single.just(remoteRecentArticleDate)
        every { localDateProvider.getDate() } returns Single.just(userLastReadArticleDate)

        mockkStatic(ActivityManager::class)
        every { getMyMemoryState(capture(processInfo)) } answers {
            processInfo.captured.importance = RunningAppProcessInfo.IMPORTANCE_GONE
        }

        // Given initialized worker, existing unread articles, and app is not in the foreground

        // When work is executed
        worker.createWork().test()

        // Then worker should get the last user read article publication date
        verify { remoteDateProvider.getDate() }

        // And get the most recent published article date by news outlet
        verify { localDateProvider.getDate() }

        // Then worker should show status bar notification on user device
        val channel = NotificationManagerCompat.from(getApplicationContext())
            .getNotificationChannel(NewsNotificationWorker.CHANNEL_ID)!!
        val notificationManager: NotificationManager = getApplicationContext<Context>()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications = notificationManager.activeNotifications

        assertThat(channel.importance).isEqualTo(NotificationManager.IMPORTANCE_DEFAULT)
        assertThat(notifications.size).isEqualTo(1)
        assertThat(notifications[0].id).isEqualTo(NewsNotificationWorker.NOTIFICATION_ID)
        assertThat(notifications[0].notification.smallIcon.resId).isEqualTo(R.drawable.ic_notification)
    }

    @Test
    fun doNotNotifyWhenInForeground() {
        // Test case fixture
        val remoteRecentArticleDate = LocalDateTime(2020,2,12,12,15).toDate()
        val userLastReadArticleDate = LocalDateTime(2020,2,11,12,15).toDate()
        val processInfo = slot<RunningAppProcessInfo>()

        every { remoteDateProvider.getDate() } returns Single.just(remoteRecentArticleDate)
        every { localDateProvider.getDate() } returns Single.just(userLastReadArticleDate)

        mockkStatic(ActivityManager::class)
        every { getMyMemoryState(capture(processInfo)) } answers {
            processInfo.captured.importance = RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }

        // Given initialized worker, existing unread articles, and app is in the foreground

        // When work is executed
        worker.createWork().test()

        // Then worker should not show status bar notification on user device
        val notificationManager: NotificationManager = getApplicationContext<Context>()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications = notificationManager.activeNotifications

        assertThat(notifications.size).isEqualTo(0)
    }

    @Test
    fun doNotNotifyWhenUnreadArticlesDoNotExist() {
        // Test case fixture
        val remoteRecentArticleDate = LocalDateTime(2020,2,11,12,15).toDate()
        val userLastReadArticleDate = LocalDateTime(2020,2,11,12,15).toDate()
        val processInfo = slot<RunningAppProcessInfo>()

        every { remoteDateProvider.getDate() } returns Single.just(remoteRecentArticleDate)
        every { localDateProvider.getDate() } returns Single.just(userLastReadArticleDate)

        mockkStatic(ActivityManager::class)
        every { getMyMemoryState(capture(processInfo)) } answers {
            processInfo.captured.importance = RunningAppProcessInfo.IMPORTANCE_GONE
        }

        // Given initialized worker and no existing unread articles and app is not in the foreground

        // When work is executed
        worker.createWork().test()

        // Then worker should get the last user read article publication date
        verify { remoteDateProvider.getDate() }

        // And get the most recent published article date by news outlet
        verify { localDateProvider.getDate() }

        // Then worker should not show status bar notification on user device
        val notificationManager: NotificationManager = getApplicationContext<Context>()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications = notificationManager.activeNotifications

        assertThat(notifications.size).isEqualTo(0)
    }

    @Test
    fun propagateFailedResultWhenWorkFail() {
        // Test case fixture
        val remoteSubject = SingleSubject.create<Date>()
        val processInfo = slot<RunningAppProcessInfo>()

        every { remoteDateProvider.getDate() } returns remoteSubject
        every { localDateProvider.getDate() } returns Single.just(LocalDateTime.now().toDate())

        mockkStatic(ActivityManager::class)
        every { getMyMemoryState(capture(processInfo)) } answers {
            processInfo.captured.importance = RunningAppProcessInfo.IMPORTANCE_GONE
        }

        // Given initialized worker, existing unread articles, and app is not in the foreground

        // When work is executed
        val observer = worker.createWork().test()

        // And work execution fail
        remoteSubject.onError(Throwable())

        // Then worker should propagate expected failure result to observable chain
        observer.assertValue(Result.failure())
    }

    @Test
    fun openAppMainWhenUserTapNotification() {
        // Test case fixture
        val remoteRecentArticleDate = LocalDateTime(2020,2,12,12,15).toDate()
        val userLastReadArticleDate = LocalDateTime(2020,2,11,12,15).toDate()
        val processInfo = slot<RunningAppProcessInfo>()

        every { remoteDateProvider.getDate() } returns Single.just(remoteRecentArticleDate)
        every { localDateProvider.getDate() } returns Single.just(userLastReadArticleDate)

        mockkStatic(ActivityManager::class)
        every { getMyMemoryState(capture(processInfo)) } answers {
            processInfo.captured.importance = RunningAppProcessInfo.IMPORTANCE_GONE
        }

        // Given initialized worker, existing unread articles, and app is not in the foreground

        // When work is executed
        worker.createWork().test()

        // Then worker should set content intent on notification to open app main activity
        val notificationManager: NotificationManager = getApplicationContext<Context>()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications = notificationManager.activeNotifications

        val app = getApplicationContext<Context>()
        assertThat(notifications[0].notification.contentIntent.creatorPackage).isEqualTo(app.packageName)
    }

    @Test
    fun addVibrationWhenVibrationEnableAndNotificationShown() {
        // Test case fixture
        val remoteRecentArticleDate = LocalDateTime(2020,2,12,12,15).toDate()
        val userLastReadArticleDate = LocalDateTime(2020,2,11,12,15).toDate()
        val processInfo = slot<RunningAppProcessInfo>()

        every { remoteDateProvider.getDate() } returns Single.just(remoteRecentArticleDate)
        every { localDateProvider.getDate() } returns Single.just(userLastReadArticleDate)

        mockkStatic(ActivityManager::class)
        every { getMyMemoryState(capture(processInfo)) } answers {
            processInfo.captured.importance = RunningAppProcessInfo.IMPORTANCE_GONE
        }

        worker = TestListenableWorkerBuilder<NewsNotificationWorker>(
            context = getApplicationContext(),
            inputData = Data.Builder()
                .putBoolean(NewsNotificationWorker.KEY_VIBRATION,true).build()
        )
            .setWorkerFactory(workerFactory)
            .build()

        // Given initialized worker,existing unread articles, app is not in the
        // foreground, and enabled vibration for notification

        // When work is executed
        worker.createWork().test()

        // Then worker should not set status bar notification on user device with a vibration pattern
        val channel = NotificationManagerCompat.from(getApplicationContext())
            .getNotificationChannel(NewsNotificationWorker.CHANNEL_ID)!!

        assertThat(channel.vibrationPattern).isEqualTo(NewsNotificationWorker.VIBRATION_PATTERN)
    }
}