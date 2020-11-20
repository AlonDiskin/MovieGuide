package com.diskin.alon.movieguide.reviews.di

import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [ReviewsFeatureModule::class])
interface MovieReviewActivitySubcomponent : AndroidInjector<MovieReviewActivity> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MovieReviewActivity>
}