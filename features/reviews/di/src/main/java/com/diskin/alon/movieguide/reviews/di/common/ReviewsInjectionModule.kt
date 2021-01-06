package com.diskin.alon.movieguide.reviews.di.common

import com.diskin.alon.movieguide.reviews.di.moviescontainer.MoviesFragmentSubComponent
import com.diskin.alon.movieguide.reviews.di.reviewcontainer.MovieReviewActivitySubComponent
import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [
    MovieReviewActivitySubComponent::class,
    MoviesFragmentSubComponent::class
])
abstract class ReviewsInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(MoviesFragment::class)
    abstract fun bindMoviesFragmentAndroidInjectorFactory(factory: MoviesFragmentSubComponent.Factory): AndroidInjector.Factory<*>

    @Binds
    @IntoMap
    @ClassKey(MovieReviewActivity::class)
    abstract fun bindMovieReviewActivityAndroidInjectorFactory(factory: MovieReviewActivitySubComponent.Factory): AndroidInjector.Factory<*>
}