package com.diskin.alon.movieguide.journeysteps

import androidx.test.filters.LargeTest
import com.diskin.alon.movieguide.util.MockWebServerRule
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Step definitions runner for app navigation journey scenario.
 */
@RunWith(Parameterized::class)
@LargeTest
class AppNavStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/app_nav_journey.feature")
                .scenarios()
        }
    }

    @JvmField
    @Rule
    val serverRule = MockWebServerRule()

    @Test
    fun test() {
        start(AppNavSteps())
    }
}