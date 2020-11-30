package com.diskin.alon.movieguide.news.featuretesting.listbookmarks

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.filters.MediumTest
import com.diskin.alon.movieguide.news.featuretesting.TestApp
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.Scenario
import com.mauriciotogneri.greencoffee.ScenarioConfig
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.*

/**
 * Step definitions runner for 'Bookmarks listed' scenario.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = TestApp::class, sdk = [28])
@MediumTest
class ListBookmarksStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("feature/bookmarks_browsing.feature")
                .withTags("@list-bookmarks")
                .scenarios()

            for (scenarioConfig in scenarioConfigs) {
                res.add(arrayOf(scenarioConfig))
            }

            return res
        }
    }

    @Test
    fun test() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        val testApp = getApplicationContext<Context>() as TestApp
        start(
            ListBookmarksSteps(
                testApp.getMockWebServer(),
                testApp.getTestDatabase()
            )
        )
    }

    override fun afterScenarioEnds(scenario: Scenario?, locale: Locale?) {
        super.afterScenarioEnds(scenario, locale)
        val testApp = getApplicationContext<Context>() as TestApp

        testApp.getMockWebServer().shutdown()
        testApp.getTestDatabase().close()
    }
}