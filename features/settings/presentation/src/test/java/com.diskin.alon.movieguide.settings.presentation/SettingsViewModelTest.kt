package com.diskin.alon.movieguide.settings.presentation

import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.settings.appservices.data.AppTheme
import com.diskin.alon.movieguide.settings.appservices.data.NewsNotificationConfig
import com.diskin.alon.movieguide.settings.presentation.data.AppThemeModelRequest
import com.diskin.alon.movieguide.settings.presentation.data.ConfigNewsNotificationModelRequest
import com.diskin.alon.movieguide.settings.presentation.viewmodel.SettingsViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * [SettingsViewModel] unit test class.
 */
class SettingsViewModelTest {

    // Test subject
    private lateinit var viewModel: SettingsViewModel

    // Collaborators
    private val model: Model = mockk()

    @Before
    fun setUp() {
        viewModel = SettingsViewModel(model)
    }

    @Test
    fun setModelAppTheme() {
        // Test case fixture
        every { model.execute(any<AppThemeModelRequest>()) } returns Unit

        // Given

        // When
        val appTheme: AppTheme = mockk()
        viewModel.setAppTheme(appTheme)

        // Then
        val expected = AppThemeModelRequest(appTheme)
        verify { model.execute(expected) }
    }

    @Test
    fun configModelNewsUpdateNotification() {
        // Test case fixture
        every { model.execute(any<ConfigNewsNotificationModelRequest>()) } returns Unit

        // Given

        // When
        val config: NewsNotificationConfig = mockk()
        viewModel.configNewsUpdateNotification(config)

        // Then
        val expected = ConfigNewsNotificationModelRequest(config)
        verify { model.execute(expected) }
    }
}