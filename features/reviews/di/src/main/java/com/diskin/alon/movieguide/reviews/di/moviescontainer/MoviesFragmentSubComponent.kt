package com.diskin.alon.movieguide.reviews.di.moviescontainer

import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [MoviesModule::class])
interface MoviesFragmentSubComponent : AndroidInjector<MoviesFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MoviesFragment>
}