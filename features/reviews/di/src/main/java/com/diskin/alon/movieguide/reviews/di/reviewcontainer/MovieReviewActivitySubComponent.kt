package com.diskin.alon.movieguide.reviews.di.reviewcontainer

import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [MovieReviewModule::class])
interface MovieReviewActivitySubComponent : AndroidInjector<MovieReviewActivity> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MovieReviewActivity>
}