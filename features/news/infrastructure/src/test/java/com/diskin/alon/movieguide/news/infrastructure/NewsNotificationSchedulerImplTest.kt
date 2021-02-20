package com.diskin.alon.movieguide.news.infrastructure

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.diskin.alon.movieguide.news.appservices.data.NewsNotificationData
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationSchedulerImpl.Companion.NEWS_NOTIFICATION_WORK_NAME
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationSchedulerImpl.Companion.WORK_INTERVAL_HOURS
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationWorker.Companion.KEY_VIBRATION
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * [NewsNotificationSchedulerImpl] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class NewsNotificationSchedulerImplTest {

    // System under test
    private lateinit var scheduler: NewsNotificationSchedulerImpl
    private val workManager: WorkManager = mockk()

    @Before
    fun setUp() {
        // Initialize scheduler
        scheduler = NewsNotificationSchedulerImpl(workManager)
    }

    @Test
    fun disableScheduledServiceWhenCanceled() {
        // Test case fixture
        every { workManager.cancelUniqueWork(any()) } returns mockk()

        // Given an initialized scheduler

        // When scheduler asked to cancel the news notification
        scheduler.cancel().test()

        // Then notificationManager should cancel the scheduled notification work on workManager
        verify { workManager.cancelUniqueWork(NEWS_NOTIFICATION_WORK_NAME) }
    }

    @Test
    @Parameters("vibrationConfigParams")
    fun scheduleNotificationVibrationAccordingToClientWhenScheduled(vibrationConfig: Boolean) {
        // Test case fixture
        val workRequestSlot = slot<PeriodicWorkRequest>()

        every { workManager.enqueueUniquePeriodicWork(any(),any(),capture(workRequestSlot)) } returns mockk()

        // Given an initialized scheduler

        // When scheduler is asked to schedule the news notification
        scheduler.schedule(NewsNotificationData(true,vibrationConfig)).test()

        // Then scheduler should set notification vibration config to work input data
        assertThat(workRequestSlot.captured.workSpec.input.getBoolean(KEY_VIBRATION,false))
            .isEqualTo(vibrationConfig)
    }

    @Test
    fun scheduleNotificationToRunWhenConnectedWhenScheduled() {
        // Test case fixture
        val workRequestSlot = slot<PeriodicWorkRequest>()

        every { workManager.enqueueUniquePeriodicWork(any(),any(),capture(workRequestSlot)) } returns mockk()

        // Given an initialized scheduler

        // When scheduler is asked to schedule the news notification
        val notificationData = NewsNotificationData(enabled = true, vibrate = true)
        scheduler.schedule(notificationData).test()

        // Then manager should enqueue work request with connectivity constrain
        assertThat(workRequestSlot.captured.workSpec.constraints.requiredNetworkType)
            .isEqualTo(NetworkType.CONNECTED)
    }

    @Test
    fun scheduleRepeatableNotificationWhenScheduled() {
        // Test case fixture
        val workRequestSlot = slot<PeriodicWorkRequest>()

        every { workManager.enqueueUniquePeriodicWork(any(),any(),capture(workRequestSlot)) } returns mockk()

        // Given an initialized scheduler

        // When scheduler is asked to schedule the news notification
        val notificationData = NewsNotificationData(enabled = true, vibrate = true)
        scheduler.schedule(notificationData).test()

        // Then manager should enqueue repeatable unique work
        assertThat(workRequestSlot.captured.workSpec.isPeriodic).isTrue()
        assertThat(workRequestSlot.captured.workSpec.intervalDuration)
            .isEqualTo(TimeUnit.HOURS.toMillis(WORK_INTERVAL_HOURS))
    }

    @Test
    fun cancelAnyPreviousScheduledNotificationWhenScheduled() {
        // Test case fixture
        val periodicPolicySlot = slot<ExistingPeriodicWorkPolicy>()

        every { workManager.enqueueUniquePeriodicWork(any(),capture(periodicPolicySlot),any()) } returns mockk()

        // Given an initialized scheduler

        // When scheduler is asked to schedule the news notification
        val notificationData = NewsNotificationData(enabled = true, vibrate = true)
        scheduler.schedule(notificationData).test()

        // Then manager should enqueue unique work with replace policy
        assertThat(periodicPolicySlot.captured).isEqualTo(ExistingPeriodicWorkPolicy.REPLACE)
    }

    private fun vibrationConfigParams() = arrayOf(true,false)
}