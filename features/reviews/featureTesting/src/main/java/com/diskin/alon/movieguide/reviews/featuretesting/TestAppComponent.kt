package com.diskin.alon.movieguide.reviews.featuretesting

import android.app.Application
import com.diskin.alon.movieguide.reviews.di.MovieReviewActivityInjectionModule
import com.diskin.alon.movieguide.reviews.di.MoviesFragmentInjectionModule
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
    TestNetworkingModule::class,
    MoviesFragmentInjectionModule::class,
    MovieReviewActivityInjectionModule::class
])
interface TestAppComponent : AndroidInjector<TestApp> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance app: Application): TestAppComponent
    }

    fun getMockWebServer(): MockWebServer
}