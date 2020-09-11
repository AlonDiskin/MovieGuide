package com.diskin.alon.movieguide.home.di

import com.diskin.alon.movieguide.home.presentation.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityInjectionModule {

    @ContributesAndroidInjector
    abstract fun contributeYourAndroidInjector(): MainActivity
}