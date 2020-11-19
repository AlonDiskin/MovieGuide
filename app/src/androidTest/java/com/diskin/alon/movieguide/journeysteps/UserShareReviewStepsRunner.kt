package com.diskin.alon.movieguide.journeysteps

import androidx.test.filters.LargeTest
import com.diskin.alon.movieguide.util.MockWebServerRule
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import com.squareup.rx2.idler.Rx2Idler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Step definitions runner for 'User share review' scenario.
 */
@RunWith(Parameterized::class)
@LargeTest
class UserShareReviewStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/user_share_movie_review_journey.feature")
                .scenarios()
        }
    }

    @JvmField
    @Rule
    val serverRule = MockWebServerRule()

    @Test
    fun test() {
        // Init RxIdler
        RxJavaPlugins.setInitIoSchedulerHandler(
            Rx2Idler.create("RxJava 2.x IO Scheduler"))

        start(UserShareReviewSteps())
    }
}