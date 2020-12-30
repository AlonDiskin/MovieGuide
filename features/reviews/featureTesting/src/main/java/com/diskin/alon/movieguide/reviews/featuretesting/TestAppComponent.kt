package com.diskin.alon.movieguide.reviews.featuretesting

import android.app.Application
import com.diskin.alon.movieguide.reviews.di.common.ReviewsDataModule
import com.diskin.alon.movieguide.reviews.di.common.ReviewsInjectionModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.mockwebserver.MockWebServer
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AndroidInjectionModule::class,
    TestAppModule::class,
    ReviewsInjectionModule::class,
    ReviewsDataModule::class
])
interface TestAppComponent : AndroidInjector<TestApp> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance app: Application): TestAppComponent
    }

    fun getMockWebServer(): MockWebServer

    fun getTestDatabase(): TestDatabase
}