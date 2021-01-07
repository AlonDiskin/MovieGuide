package com.diskin.alon.movieguide.reviews.di.common

import com.diskin.alon.movieguide.reviews.di.moviescontainer.MoviesFragmentSubComponent
import com.diskin.alon.movieguide.reviews.di.moviessearchcontainer.MoviesSearchFragmentSubComponent
import com.diskin.alon.movieguide.reviews.di.reviewcontainer.MovieReviewActivitySubComponent
import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesSearchFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [
    MovieReviewActivitySubComponent::class,
    MoviesFragmentSubComponent::class,
    MoviesSearchFragmentSubComponent::class
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

    @Binds
    @IntoMap
    @ClassKey(MoviesSearchFragment::class)
    abstract fun bindMoviesSearchFragmentAndroidInjectorFactory(factory: MoviesSearchFragmentSubComponent.Factory): AndroidInjector.Factory<*>
}