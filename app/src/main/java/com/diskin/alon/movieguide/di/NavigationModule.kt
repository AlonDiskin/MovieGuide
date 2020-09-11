package com.diskin.alon.movieguide.di

import com.diskin.alon.movieguide.AppNavigator
import com.diskin.alon.movieguide.home.presentation.HomeNavigator
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class NavigationModule {

    @Singleton
    @Binds
    abstract fun bindHomeNavigator(navigator: AppNavigator): HomeNavigator
}