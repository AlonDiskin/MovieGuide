package com.diskin.alon.movieguide.reviews.di

import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [MoviesFragmentSubcomponent::class])
abstract class MoviesFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(MoviesFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: MoviesFragmentSubcomponent.Factory): AndroidInjector.Factory<*>
}