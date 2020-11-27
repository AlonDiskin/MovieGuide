package com.diskin.alon.movieguide.news.di.headlinescontainer

import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [HeadlinesModule::class])
interface HeadlinesFragmentSubComponent : AndroidInjector<HeadlinesFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<HeadlinesFragment>
}