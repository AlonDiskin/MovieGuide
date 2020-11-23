package com.diskin.alon.movieguide.news.featuretesting

import android.app.Application
import com.diskin.alon.movieguide.news.di.entryinjection.NewsInjectionModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.mockwebserver.MockWebServer
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    NewsInjectionModule::class,
    TestAppModule::class
])
interface TestAppComponent : AndroidInjector<TestApp> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance app: Application): TestAppComponent
    }

    fun getMockWebServer(): MockWebServer

    fun getTestDatabase(): TestDatabase
}