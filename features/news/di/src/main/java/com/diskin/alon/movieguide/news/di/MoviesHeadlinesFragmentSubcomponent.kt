package com.diskin.alon.movieguide.news.di


import com.diskin.alon.movieguide.news.presentation.controller.MoviesHeadlinesFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [NewsFeatureModule::class])
interface MoviesHeadlinesFragmentSubcomponent : AndroidInjector<MoviesHeadlinesFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MoviesHeadlinesFragment>
}