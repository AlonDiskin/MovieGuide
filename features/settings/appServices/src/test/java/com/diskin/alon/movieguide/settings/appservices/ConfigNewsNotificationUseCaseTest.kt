package com.diskin.alon.movieguide.settings.appservices

import com.diskin.alon.movieguide.settings.appservices.data.ConfigNewsNotificationRequest
import com.diskin.alon.movieguide.settings.appservices.interfaces.NewsUpdateNotificationManager
import com.diskin.alon.movieguide.settings.appservices.usecase.ConfigNewsNotificationUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * [ConfigNewsNotificationUseCase] unit test class.
 */
class ConfigNewsNotificationUseCaseTest {

    // Test subject
    private lateinit var useCase: ConfigNewsNotificationUseCase

    // Collaborators
    private val manager: NewsUpdateNotificationManager = mockk()

    @Before
    fun setUp() {
        useCase = ConfigNewsNotificationUseCase(manager)
    }

    @Test
    fun configActivationWhenExecuted() {
        // Test case fixture
        every { manager.config(any()) } returns Unit

        // Given

        // When use case is executed
        val request = ConfigNewsNotificationRequest(mockk())
        useCase.execute(request)

        // Then use case set enabled state on manager according o request param
        verify { manager.config(request.config) }
    }
}