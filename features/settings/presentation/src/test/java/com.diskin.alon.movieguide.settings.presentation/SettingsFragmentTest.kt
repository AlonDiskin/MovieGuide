package com.diskin.alon.movieguide.settings.presentation

import android.content.Context
import android.os.Looper
import androidx.preference.PreferenceManager
import androidx.lifecycle.ViewModelLazy
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.preference.ListPreference
import androidx.preference.SwitchPreference
import androidx.preference.get
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.settings.appservices.data.AppTheme
import com.diskin.alon.movieguide.settings.appservices.data.NewsNotificationConfig
import com.diskin.alon.movieguide.settings.presentation.controller.SettingsFragment
import com.diskin.alon.movieguide.settings.presentation.viewmodel.SettingsViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

/**
 * [SettingsFragment] hermetic ui test.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(sdk = [28])
class SettingsFragmentTest {

    // Test subject
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val viewModel = mockk<SettingsViewModel>()

    // Test nav controller
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<*>>().value } returns viewModel

        // Setup test nav controller
        navController.setGraph(R.navigation.settings_nav_graph)

        // Launch fragment under test
        scenario = launchFragmentInHiltContainer<SettingsFragment>()

        // Set the NavController property on the fragment with test controller
        scenario.onActivity {
            Navigation.setViewNavController(
                it.supportFragmentManager.fragments[0].requireView(),
                navController)
        }
    }

    @Test
    fun showAppNameInAppBarTitle() {
        // Given a resumed fragment

        // Then fragment should show app name in title in appbar
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertThat(navController.currentDestination?.label).isEqualTo(appName)
    }

    @Test
    fun showDefaultThemeWhenPrefNotModified() {
        // Given a resumed fragment that has not been used to modify app theme pref

        scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.fragments[0] as SettingsFragment
            val key = fragment.getString(R.string.pref_theme_key)
            val themePref = fragment.preferenceScreen.get<ListPreference>(key)!!
            val defaultThemeEntry = fragment.getString(R.string.pref_theme_day_entry)

            // Then fragment should show default theme preference value
            assertThat(themePref.isShown)
            assertThat(themePref.summary).isEqualTo(defaultThemeEntry)
        }
    }

    @Test
    fun setAppThemePrefWhenSelected() {
        // Test case fixture
        every { viewModel.setAppTheme(any()) } returns Unit

        // Given a resumed fragment

        // When
        onView(withText(R.string.pref_theme_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText(R.string.pref_theme_night_entry))
            .inRoot(isDialog())
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        verify(verifyBlock = { viewModel.setAppTheme(AppTheme.DARK) },exactly = 1)

        // When
        onView(withText(R.string.pref_theme_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText(R.string.pref_theme_day_entry))
            .inRoot(isDialog())
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        verify(verifyBlock = { viewModel.setAppTheme(AppTheme.LIGHT) },exactly = 1)
    }

    @Test
    fun showDefaultNewsNotificationActivationWhenPrefNotModified() {
        // Given a resumed fragment that has not been used to modify news notification pref

        // Then fragment should show default news notification preference value
        scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.fragments[0] as SettingsFragment
            val key = fragment.getString(R.string.pref_news_notification_key)
            val prefUi = fragment.preferenceScreen.get<SwitchPreference>(key)!!

            val defaultValue = fragment.getString(R.string.pref_news_notification_default_value).toBoolean()

            assertThat(prefUi.isChecked).isEqualTo(defaultValue)
        }
    }

    @Test
    fun enableNewsUpdateNotificationWhenUserSwitchDisabledPrefOn() {
        // Test case fixture
        every { viewModel.configNewsUpdateNotification(any()) } returns Unit

        // Given a disabled news update notification pref

        // When user enables notification
        onView(withText(R.string.pref_news_notification_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should enable it with all its current prefs configurations
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val vibrationKey = context.getString(R.string.pref_news_notification_vibration_key)
        val vibrationPref = sp.getBoolean(vibrationKey,false)
        val expected = NewsNotificationConfig(true,vibrationPref)

        verify { viewModel.configNewsUpdateNotification(expected) }
    }

    @Test
    fun disableNewsUpdateNotificationWhenUserSwitchEnabledPrefOff() {
        // Test case fixture
        every { viewModel.configNewsUpdateNotification(any()) } returns Unit

        // Given a enabled news update notification pref
        onView(withText(R.string.pref_news_notification_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When
        onView(withText(R.string.pref_news_notification_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        val expected = NewsNotificationConfig(false)
        verify { viewModel.configNewsUpdateNotification(expected) }
    }

    @Test
    fun setNotificationVibrationAsNotificationPrefDependent() {
        // Given

        // Then
        scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.fragments[0] as SettingsFragment
            val vibrationPreKey = fragment.getString(R.string.pref_news_notification_vibration_key)
            val vibrationPrefUi = fragment.preferenceScreen.get<SwitchPreference>(vibrationPreKey)!!
            val notificationKey = fragment.getString(R.string.pref_news_notification_key)

            assertThat(vibrationPrefUi.dependency).isEqualTo(notificationKey)
        }
    }

    @Test
    fun enableNewsUpdateNotificationVibrationWhenUserSwitchDisabledPrefOn() {
        // Test case fixture
        every { viewModel.configNewsUpdateNotification(any()) } returns Unit

        // Given a disabled notification vibration pref and enabled notification pref
        onView(withText(R.string.pref_news_notification_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When
        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(4))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText(R.string.pref_news_notification_vibration_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        val expected = NewsNotificationConfig(enabled = true, vibrate = true)
        verify { viewModel.configNewsUpdateNotification(expected) }
    }

    @Test
    fun disableNewsUpdateNotificationVibrationWhenUserSwitchPrefOff() {
        // Test case fixture
        every { viewModel.configNewsUpdateNotification(any()) } returns Unit

        // Given a enabled notification vibration pref and enabled notification pref
        onView(withText(R.string.pref_news_notification_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(4))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When
        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(4))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText(R.string.pref_news_notification_vibration_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        val expected = NewsNotificationConfig(enabled = true, vibrate = false)
        verify { viewModel.configNewsUpdateNotification(expected) }
    }

}