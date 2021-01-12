package com.diskin.alon.movieguide.reviews.di.moviessearchcontainer

import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesSearchFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [MoviesSearchModule::class])
interface MoviesSearchFragmentSubComponent : AndroidInjector<MoviesSearchFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MoviesSearchFragment>
}