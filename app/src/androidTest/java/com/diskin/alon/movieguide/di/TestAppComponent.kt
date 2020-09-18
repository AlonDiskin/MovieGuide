package com.diskin.alon.movieguide.di

import com.diskin.alon.movieguide.home.di.MainActivityInjectionModule
import com.diskin.alon.movieguide.news.di.MoviesHeadlinesFragmentInjectionModule
import com.diskin.alon.movieguide.runner.TestApp
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class,
    NavigationModule::class,
    MainActivityInjectionModule::class,
    MoviesHeadlinesFragmentInjectionModule::class,
    TestNetworkingModule::class
])
interface TestAppComponent : AndroidInjector<TestApp>