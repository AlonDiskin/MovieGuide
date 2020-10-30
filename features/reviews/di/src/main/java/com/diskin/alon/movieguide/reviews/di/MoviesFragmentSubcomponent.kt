package com.diskin.alon.movieguide.reviews.di

import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [ReviewsFeatureModule::class])
interface MoviesFragmentSubcomponent : AndroidInjector<MoviesFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MoviesFragment>
}