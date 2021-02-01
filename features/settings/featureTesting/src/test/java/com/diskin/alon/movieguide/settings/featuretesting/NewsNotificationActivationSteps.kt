package com.diskin.alon.movieguide.settings.featuretesting

import android.content.Context
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.common.util.messaging.NewsUpdateConfigEvent
import com.diskin.alon.movieguide.settings.presentation.controller.SettingsFragment
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.every
import io.mockk.verify
import org.greenrobot.eventbus.EventBus
import org.robolectric.Shadows

/**
 * Step definitions runner for 'User config notification activation' scenario.
 */
class NewsNotificationActivationSteps(private val mockEventBus: EventBus) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private val context = ApplicationProvider.getApplicationContext<Context>()

    init {
        every { mockEventBus.post(any()) } returns Unit
    }

    @Given("^User has not changed news notification setting from default$")
    fun user_has_not_changed_news_notification_setting_from_default() {
        clearSharedPrefs()
    }

    @When("^User open settings screen$")
    fun user_open_settings_screen() {
        scenario = launchFragmentInHiltContainer<SettingsFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^News notification should be disabled$")
    fun news_notification_should_be_disabled() {
        val key = context.getString(R.string.pref_news_notification_key)
        val prefValue = getSharedPrefs().getBoolean(key,true)

        assertThat(prefValue).isFalse()
    }

    @When("^User enables news update notification$")
    fun user_enables_news_update_notification() {
        onView(withText(R.string.pref_news_notification_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^App should enable news update notification service$")
    fun app_should_enable_news_update_notification_service() {
        val event = getExpectedEnableNewsUpdateConfigEvent()
        verify { mockEventBus.post(event) }
    }

    @When("^User disable news notification update$")
    fun user_disable_news_notification_update() {
        onView(withText(R.string.pref_news_notification_title))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^App should disable news update notification service$")
    fun app_should_disable_news_update_notification_service() {
        val event = getExpectedDisableNewsUpdateConfigEvent()
        verify { mockEventBus.post(event) }
    }

    private fun getExpectedEnableNewsUpdateConfigEvent(): NewsUpdateConfigEvent {
        val vibrationKey = context.getString(R.string.pref_news_notification_vibration_key)
        val prefVibration = getSharedPrefs().getBoolean(vibrationKey,false)

        return NewsUpdateConfigEvent(true,prefVibration)
    }

    private fun getExpectedDisableNewsUpdateConfigEvent(): NewsUpdateConfigEvent {
        val vibrationKey = context.getString(R.string.pref_news_notification_vibration_key)
        val prefVibration = getSharedPrefs().getBoolean(vibrationKey,false)

        return NewsUpdateConfigEvent(false,prefVibration)
    }
}