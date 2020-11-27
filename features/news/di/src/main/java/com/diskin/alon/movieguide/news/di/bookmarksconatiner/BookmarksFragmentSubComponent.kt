package com.diskin.alon.movieguide.news.di.bookmarksconatiner


import com.diskin.alon.movieguide.news.presentation.controller.BookmarksFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [BookmarksModule::class])
interface BookmarksFragmentSubComponent : AndroidInjector<BookmarksFragment> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<BookmarksFragment>
}