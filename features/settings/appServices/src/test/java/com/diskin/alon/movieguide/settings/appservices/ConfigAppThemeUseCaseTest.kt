package com.diskin.alon.movieguide.settings.appservices

import com.diskin.alon.movieguide.settings.appservices.data.AppTheme
import com.diskin.alon.movieguide.settings.appservices.data.ConfigAppThemeRequest
import com.diskin.alon.movieguide.settings.appservices.interfaces.AppThemeManager
import com.diskin.alon.movieguide.settings.appservices.usecase.ConfigAppThemeUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * [ConfigAppThemeUseCase] unit test class.
 */
class ConfigAppThemeUseCaseTest {

    // Test subject
    private lateinit var useCase: ConfigAppThemeUseCase

    // Collaborators
    private val themeManager: AppThemeManager = mockk()

    @Before
    fun setUp() {
        useCase = ConfigAppThemeUseCase(themeManager)
    }

    @Test
    fun configThemeWhenExecuted() {
        // Test case fixture
        every { themeManager.setTheme(any()) } returns Unit

        // Given an initialized use case

        // When use case is executed
        val theme: AppTheme = mockk()
        useCase.execute(ConfigAppThemeRequest(theme))

        // Then use case should set given theme on theme manager
        verify { themeManager.setTheme(theme) }
    }
}