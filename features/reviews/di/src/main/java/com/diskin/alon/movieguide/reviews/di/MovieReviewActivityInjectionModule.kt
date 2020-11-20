package com.diskin.alon.movieguide.reviews.di

import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [MovieReviewActivitySubcomponent::class])
abstract class MovieReviewActivityInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(MovieReviewActivity::class)
    abstract fun bindAndroidInjectorFactory(factory: MovieReviewActivitySubcomponent.Factory): AndroidInjector.Factory<*>
}