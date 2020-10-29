package com.diskin.alon.movieguide.news.featuretesting

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import okhttp3.mockwebserver.MockWebServer
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class TestApp : DaggerApplication(), TestLifecycleApplication {
    private lateinit var testAppComponent: TestAppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        testAppComponent = DaggerTestAppComponent.factory().create(this)
        return testAppComponent
    }

    override fun beforeTest(method: Method?) {

    }

    override fun prepareTest(test: Any?) {

    }

    override fun afterTest(method: Method?) {

    }

    fun getMockWebServer(): MockWebServer {
        return testAppComponent.getMockWebServer()
    }
}