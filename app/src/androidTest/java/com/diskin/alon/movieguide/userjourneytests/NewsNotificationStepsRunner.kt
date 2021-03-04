package com.diskin.alon.movieguide.userjourneytests

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.LargeTest
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.diskin.alon.movieguide.di.NetworkingModule
import com.diskin.alon.movieguide.news.di.common.NewsNetworkingModule
import com.diskin.alon.movieguide.news.infrastructure.LocalRecentDateProvider
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationWorker
import com.diskin.alon.movieguide.news.infrastructure.RemoteRecentDateProvider
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
import javax.inject.Inject

/**
 * Step definitions runner for 'User search for movie' scenario.
 */
@HiltAndroidTest
@UninstallModules(NetworkingModule::class, ReviewsNetworkingModule::class, NewsNetworkingModule::class)
@RunWith(Parameterized::class)
@LargeTest
class NewsNotificationStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/user_notified_for_unread_articles.feature")
                .scenarios()
        }
    }

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var remoteProvider: RemoteRecentDateProvider

    @Inject
    lateinit var localProvider: LocalRecentDateProvider

    @Test
    fun test() {
        hiltRule.inject()

        val workerFactory = TestWorkerFactory(remoteProvider, localProvider)
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .setWorkerFactory(workerFactory)
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(ApplicationProvider.getApplicationContext(), config)
        start(NewsNotificationSteps(NetworkUtil.server,workerFactory))
    }

    class TestWorkerFactory(
        private val remoteProvider: RemoteRecentDateProvider,
        private val localProvider: LocalRecentDateProvider
    ) : WorkerFactory() {
        lateinit var worker: NewsNotificationWorker

        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            worker = NewsNotificationWorker(
                appContext,
                workerParameters,
                remoteProvider,
                localProvider
            )

            return worker
        }
    }
}