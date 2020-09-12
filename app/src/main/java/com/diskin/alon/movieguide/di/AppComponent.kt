package com.diskin.alon.movieguide.di

import com.diskin.alon.movieguide.MovieGuideApp
import com.diskin.alon.movieguide.home.di.MainActivityInjectionModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    NavigationModule::class,
    MainActivityInjectionModule::class])
interface AppComponent : AndroidInjector<MovieGuideApp> {
}