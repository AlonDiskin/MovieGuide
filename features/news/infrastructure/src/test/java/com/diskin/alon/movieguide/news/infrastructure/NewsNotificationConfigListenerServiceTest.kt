package com.diskin.alon.movieguide.news.infrastructure

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.common.util.messaging.NewsNotificationConfigEvent
import com.diskin.alon.movieguide.news.appservices.data.ScheduleNewsNotificationRequest
import com.diskin.alon.movieguide.news.appservices.usecase.ScheduleNewsNotificationUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.reactivex.subjects.CompletableSubject
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

/**
 * [NewsNotificationConfigListenerService] unit test.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class NewsNotificationConfigListenerServiceTest {

    // Test subject
    private lateinit var service: NewsNotificationConfigListenerService

    // Collaborators
    private val eventBus: EventBus = mockk()
    private val useCase = mockk<ScheduleNewsNotificationUseCase>()
    private val mapper = mockk<Mapper<NewsNotificationConfigEvent, ScheduleNewsNotificationRequest>>()

    @Before
    fun setUp() {
        // Stub mocked collaborators
        mockkStatic(EventBus::class)
        every { EventBus.getDefault() } returns eventBus
        every { eventBus.register(any()) } returns Unit
        every { eventBus.unregister(any()) } returns Unit

        // Start service under test
        service = Robolectric.setupService(NewsNotificationConfigListenerService::class.java)

        // Set injected collaborators
        service.useCase = useCase
        service.mapper = mapper

    }

    @Test
    fun registerToNewsNotificationConfigEventsWhenCreated() {
        // Given a started service

        // Then service should register event bus upon creation
        verify { eventBus.register(any<NewsNotificationConfigListenerService>()) }
    }

    @Test
    fun unregisterToNewsNotificationConfigEventsWhenDestroyed() {
        // Given a started services

        // When service is destroyed
        service.onDestroy()

        // Then service should unregister from  event bus
        verify { eventBus.unregister(any<NewsNotificationConfigListenerService>()) }
    }

    @Test
    fun scheduleNewsNotificationWhenConfigEventPublished() {
        // Test case fixture
        val scheduleRequest: ScheduleNewsNotificationRequest = mockk()

        every { useCase.execute(any()) } returns CompletableSubject.create()
        coEvery { mapper.map(any()).hint(ScheduleNewsNotificationRequest::class) } answers { scheduleRequest }

        // Given a started service

        // When notification config event is published
        val event: NewsNotificationConfigEvent = mockk()
        service.onMessageEvent(event)

        // Then service should schedule notification via scheduling use case
        verify { mapper.map(event) }
        verify { useCase.execute(scheduleRequest) }
    }
}