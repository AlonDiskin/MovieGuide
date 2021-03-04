package com.diskin.alon.movieguide.settings.featuretesting

import androidx.test.filters.MediumTest
import com.diskin.alon.movieguide.settings.di.SettingsAppModule
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import org.greenrobot.eventbus.EventBus
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.*
import javax.inject.Inject

/**
 * Step definitions runner for 'User config notification activation' scenario.
 */
@HiltAndroidTest
@UninstallModules(SettingsAppModule::class)
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = HiltTestApplication::class,sdk = [28])
@MediumTest
class NewsNotificationActivationStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("feature/news_update_notification_config.feature")
                .withTags("@service-activation")
                .scenarios()

            for (scenarioConfig in scenarioConfigs) {
                res.add(arrayOf(scenarioConfig))
            }

            return res
        }
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var eventBus: EventBus

    @Test
    fun test() {
        hiltRule.inject()
        start(NewsNotificationActivationSteps(eventBus))
    }
}