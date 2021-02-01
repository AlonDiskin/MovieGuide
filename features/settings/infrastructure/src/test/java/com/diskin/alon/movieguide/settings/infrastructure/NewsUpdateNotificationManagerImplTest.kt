package com.diskin.alon.movieguide.settings.infrastructure

import com.diskin.alon.movieguide.common.util.messaging.NewsUpdateConfigEvent
import com.diskin.alon.movieguide.settings.appservices.data.NewsNotificationConfig
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Test

/**
 * [NewsUpdateNotificationManagerImpl] unit test class.
 */
class NewsUpdateNotificationManagerImplTest {

    // Test subject
    private lateinit var manager: NewsUpdateNotificationManagerImpl

    // Collaborators
    private val eventBus: EventBus = mockk()

    @Before
    fun setUp() {
        manager = NewsUpdateNotificationManagerImpl(eventBus)
    }

    @Test
    fun configNewsUpdateNotificationAccordingToInput() {
        // Test case configuration
        every { eventBus.post(any()) } returns Unit

        // Given

        // When
        val configuration = NewsNotificationConfig(true)
        manager.config(configuration)

        // Then
        val expected = NewsUpdateConfigEvent(
            configuration.enabled,
            configuration.vibrate
        )

        verify { eventBus.post(expected) }
    }
}