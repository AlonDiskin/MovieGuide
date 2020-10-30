package com.diskin.alon.movieguide.reviews.featuretesting

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import okhttp3.mockwebserver.MockWebServer
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class TestApp : DaggerApplication(), TestLifecycleApplication {
    private val testAppComponent: TestAppComponent = DaggerTestAppComponent.create()

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return testAppComponent
    }

    fun getMockWebServer(): MockWebServer {
        return testAppComponent.getMockWebServer()
    }

    override fun beforeTest(method: Method?) {

    }

    override fun prepareTest(test: Any?) {

    }

    override fun afterTest(method: Method?) {

    }
}