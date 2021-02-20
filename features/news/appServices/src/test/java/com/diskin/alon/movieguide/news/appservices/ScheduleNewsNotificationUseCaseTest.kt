package com.diskin.alon.movieguide.news.appservices

import com.diskin.alon.movieguide.news.appservices.data.ScheduleNewsNotificationRequest
import com.diskin.alon.movieguide.news.appservices.interfaces.NewsNotificationScheduler
import com.diskin.alon.movieguide.news.appservices.usecase.ScheduleNewsNotificationUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * [ScheduleNewsNotificationUseCase] unit test class
 */
class ScheduleNewsNotificationUseCaseTest {

    // Test subject
    private lateinit var useCase: ScheduleNewsNotificationUseCase

    // Collaborators
    private val scheduler: NewsNotificationScheduler = mockk()

    @Before
    fun setUp() {
        useCase = ScheduleNewsNotificationUseCase(scheduler)
    }

    @Test
    fun scheduleNewsNotificationWhenExecutedToEnableNotification() {
        // Test case fixture
        every { scheduler.schedule(any()) } returns mockk()

        // Given an initialized use case

        // When use case is executed to schedule an enabled notification
        val notificationData = createEnabledNewsNotification()
        useCase.execute(ScheduleNewsNotificationRequest(notificationData))

        // Then use case should schedule notification
        verify { scheduler.schedule(notificationData) }
    }

    @Test
    fun cancelNewsNotificationWhenExecutedToEnableNotification() {
        // Test case fixture
        every { scheduler.cancel() } returns mockk()

        // Given an initialized use case

        // When use case is executed to disable the notification
        val notificationData = createDisabledNewsNotification()
        useCase.execute(ScheduleNewsNotificationRequest(notificationData))

        // Then use case should cancel the notification
        verify { scheduler.cancel() }
    }
}