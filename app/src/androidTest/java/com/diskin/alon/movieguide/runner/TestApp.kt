package com.diskin.alon.movieguide.runner

import com.diskin.alon.movieguide.di.DaggerTestAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class TestApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerTestAppComponent.factory().create(this)
    }
}