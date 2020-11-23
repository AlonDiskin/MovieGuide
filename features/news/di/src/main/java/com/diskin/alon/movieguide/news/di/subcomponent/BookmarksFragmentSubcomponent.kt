package com.diskin.alon.movieguide.news.di.subcomponent


import com.diskin.alon.movieguide.news.di.feature.NewsFeatureModule
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [NewsFeatureModule::class])
interface BookmarksFragmentSubcomponent : AndroidInjector<BookmarksFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<BookmarksFragment>
}