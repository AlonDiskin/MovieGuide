package com.diskin.alon.movieguide.news.featuretesting

import com.diskin.alon.movieguide.news.di.MoviesHeadlinesFragmentInjectionModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.mockwebserver.MockWebServer
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    MoviesHeadlinesFragmentInjectionModule::class,
    TestNetworkingModule::class])
interface TestAppComponent : AndroidInjector<TestApp> {

    fun getMockWebServer(): MockWebServer
}