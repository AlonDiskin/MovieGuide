package com.diskin.alon.movieguide.journeysteps

import androidx.test.filters.LargeTest
import com.diskin.alon.movieguide.util.NetworkUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Step definitions runner for 'User share article' scenario.
 */
@RunWith(Parameterized::class)
@LargeTest
class ArticleSharingStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/article_sharing_journey.feature")
                .scenarios()
        }
    }

    @Test
    fun test() {
        start(ArticleSharingSteps(NetworkUtil.server))
    }
}