package com.diskin.alon.movieguide.userjourneytests

import androidx.test.filters.LargeTest
import com.diskin.alon.movieguide.di.NetworkingModule
import com.diskin.alon.movieguide.news.di.common.NewsNetworkingModule
import com.diskin.alon.movieguide.reviews.di.common.ReviewsNetworkingModule
import com.diskin.alon.movieguide.util.NetworkUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Step definitions runner for 'User share article' scenario.
 */
@HiltAndroidTest
@UninstallModules(NetworkingModule::class, ReviewsNetworkingModule::class, NewsNetworkingModule::class)
@RunWith(Parameterized::class)
@LargeTest
class ArticleSharingStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/article_sharing.feature")
                .scenarios()
        }
    }

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Test
    fun test() {
        start(ArticleSharingSteps(NetworkUtil.server))
    }
}