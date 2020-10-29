package com.diskin.alon.movieguide.news.featuretesting

import android.app.Application
import com.diskin.alon.movieguide.news.di.ArticleActivityInjectionModule
import com.diskin.alon.movieguide.news.di.MoviesHeadlinesFragmentInjectionModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.mockwebserver.MockWebServer
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    MoviesHeadlinesFragmentInjectionModule::class,
    ArticleActivityInjectionModule::class,
    TestAppModule::class,
    TestNetworkingModule::class])
interface TestAppComponent : AndroidInjector<TestApp> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance app: Application): TestAppComponent
    }

    fun getMockWebServer(): MockWebServer
}