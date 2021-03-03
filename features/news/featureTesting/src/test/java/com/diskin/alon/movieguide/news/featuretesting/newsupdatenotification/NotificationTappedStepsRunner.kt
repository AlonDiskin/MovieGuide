package com.diskin.alon.movieguide.news.featuretesting.newsupdatenotification

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.filters.MediumTest
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.diskin.alon.movieguide.news.di.common.NewsNetworkingModule
import com.diskin.alon.movieguide.news.infrastructure.LocalRecentDateProvider
import com.diskin.alon.movieguide.news.infrastructure.NewsNotificationWorker
import com.diskin.alon.movieguide.news.infrastructure.RemoteRecentDateProvider
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockWebServer
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.*
import javax.inject.Inject

/**
 * Step definitions for 'News screen opened when notification tapped' scenario.
 */
@HiltAndroidTest
@UninstallModules(NewsNetworkingModule::class)
@RunWith(ParameterizedRobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = HiltTestApplication::class,sdk = [28])
@MediumTest
class NotificationTappedStepsRunner(scenario: ScenarioConfig) : GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data(): Collection<Array<Any>> {
            val res = ArrayList<Array<Any>>()
            val scenarioConfigs = GreenCoffeeConfig()
                .withFeatureFromAssets("feature/news_update_notification.feature")
                .withTags("@notification-tap")
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

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mockWebServer: MockWebServer

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

        WorkManagerTestInitHelper.initializeTestWorkManager(getApplicationContext(), config)
        start(NotificationTappedSteps(mockWebServer,workerFactory))
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