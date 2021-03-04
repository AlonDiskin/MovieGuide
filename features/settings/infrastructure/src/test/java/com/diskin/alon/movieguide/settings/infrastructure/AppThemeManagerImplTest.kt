package com.diskin.alon.movieguide.settings.infrastructure

import androidx.appcompat.app.AppCompatDelegate
import com.diskin.alon.movieguide.settings.appservices.data.AppTheme
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [AppThemeManagerImpl] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class AppThemeManagerImplTest {

    // Test subject
    private lateinit var themeManager: AppThemeManagerImpl

    @Before
    fun setUp() {
        themeManager = AppThemeManagerImpl()
    }

    @Test
    @Parameters(method = "themeParams")
    fun setAppThemeAccordingToParamWhenThemeSet(theme: AppTheme,appConfig: Int) {
        // Test case fixture
        mockkStatic(AppCompatDelegate::class)
        every { AppCompatDelegate.setDefaultNightMode(any()) } returns Unit

        // Given

        // When
        themeManager.setTheme(theme)

        // Then
        verify { AppCompatDelegate.setDefaultNightMode(appConfig) }
    }

    private fun themeParams() = arrayOf(
        arrayOf(AppTheme.DARK, AppCompatDelegate.MODE_NIGHT_YES),
        arrayOf(AppTheme.LIGHT, AppCompatDelegate.MODE_NIGHT_NO)
    )
}