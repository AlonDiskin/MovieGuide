package com.diskin.alon.movieguide.reviews.featuretesting

import com.diskin.alon.movieguide.reviews.di.MoviesFragmentInjectionModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.mockwebserver.MockWebServer
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    TestNetworkingModule::class,
    MoviesFragmentInjectionModule::class
])
interface TestAppComponent : AndroidInjector<TestApp> {

    fun getMockWebServer(): MockWebServer
}