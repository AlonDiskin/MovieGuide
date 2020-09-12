package com.diskin.alon.movieguide

import com.diskin.alon.movieguide.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class MovieGuideApp : DaggerApplication() {

    private val appComponent = DaggerAppComponent.create()

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }
}