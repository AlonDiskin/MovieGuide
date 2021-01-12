package com.diskin.alon.movieguide.runner

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.diskin.alon.movieguide.util.NetworkUtil
import com.squareup.rx2.idler.Rx2Idler
import io.reactivex.plugins.RxJavaPlugins

class TestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
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