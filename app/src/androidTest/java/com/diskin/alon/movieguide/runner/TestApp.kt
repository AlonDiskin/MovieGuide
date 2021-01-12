package com.diskin.alon.movieguide.runner

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.diskin.alon.movieguide.di.DaggerTestAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class TestApp : DaggerApplication(), Application.ActivityLifecycleCallbacks {

    lateinit var currentActivity: FragmentActivity

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerTestAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity as FragmentActivity
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

    }
}