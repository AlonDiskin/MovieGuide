package com.diskin.alon.movieguide.reviews.featuretesting.reviewsearching

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.diskin.alon.movieguide.reviews.featuretesting.TestApp
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.Scenario
import com.mauriciotogneri.greencoffee.ScenarioConfig
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.*

/**
 * Step definitions runner for 'User read search result review' scenario.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = TestApp::class,sdk = [28])
@MediumTest
class ReadSearchedReviewStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("feature/reviewed_movies_searching.feature")
                .withTags("@read-searched-review")
                .scenarios()

            for (scenarioConfig in scenarioConfigs) {
                res.add(arrayOf(scenarioConfig))
            }

            return res
        }

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun test() {
        val testApp = ApplicationProvider.getApplicationContext<Context>() as TestApp
        start(ReadSearchedReviewSteps(testApp.getMockWebServer()))
    }

    override fun afterScenarioEnds(scenario: Scenario?, locale: Locale?) {
        super.afterScenarioEnds(scenario, locale)
        val testApp = ApplicationProvider.getApplicationContext<Context>() as TestApp
        testApp.getMockWebServer().shutdown()
    }
}