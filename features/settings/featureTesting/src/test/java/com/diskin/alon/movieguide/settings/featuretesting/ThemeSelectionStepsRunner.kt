package com.diskin.alon.movieguide.settings.featuretesting

import androidx.test.filters.MediumTest
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.*

/**
 * Step definitions runner for 'User change app theme' scenario.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [28])
@MediumTest
class ThemeSelectionStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("feature/app_theme_selection.feature")
                .withTags("@theme-changed")
                .scenarios()

            for (scenarioConfig in scenarioConfigs) {
                res.add(arrayOf(scenarioConfig))
            }

            return res
        }
    }

    @Test
    fun test() {
        start(ThemeSelectionSteps())
    }
}