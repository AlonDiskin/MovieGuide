package com.diskin.alon.movieguide.runner

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.test.espresso.IdlingRegistry
import androidx.test.runner.AndroidJUnitRunner
import com.diskin.alon.movieguide.home.presentation.MainActivity
import com.diskin.alon.movieguide.util.DataBindingIdlingResource
import com.diskin.alon.movieguide.util.NetworkUtil
import com.squareup.rx2.idler.Rx2Idler
import dagger.hilt.android.testing.HiltTestApplication
import io.reactivex.plugins.RxJavaPlugins

class TestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        val app =  super.newApplication(cl, HiltTestApplication::class.java.name, context)

        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            private lateinit var dataBindingIdlingResource: DataBindingIdlingResource

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity::class.java.simpleName == MainActivity::class.java.simpleName) {
                    dataBindingIdlingResource = DataBindingIdlingResource(activity as FragmentActivity)
                    IdlingRegistry.getInstance().register(dataBindingIdlingResource)
                }
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                if (activity::class.java.simpleName == MainActivity::class.java.simpleName) {
                    IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
                }
            }
        })

        return app
    }

    override fun onStart() {
        // Init RxIdler
        RxJavaPlugins.setInitIoSchedulerHandler(
            Rx2Idler.create("RxJava 2.x IO Scheduler"))
        // Start test server
        NetworkUtil.initServer()
        super.onStart()
    }

    override fun onDestroy() {
        NetworkUtil.server.shutdown()
        super.onDestroy()
    }
}