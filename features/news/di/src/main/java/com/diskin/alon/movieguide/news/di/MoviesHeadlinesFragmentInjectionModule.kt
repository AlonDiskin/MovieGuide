package com.diskin.alon.movieguide.news.di

import com.diskin.alon.movieguide.news.presentation.MoviesHeadlinesFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [MoviesHeadlinesFragmentSubcomponent::class])
abstract class MoviesHeadlinesFragmentInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(MoviesHeadlinesFragment::class)
    abstract fun bindAndroidInjectorFactory(factory: MoviesHeadlinesFragmentSubcomponent.Factory): AndroidInjector.Factory<*>
}