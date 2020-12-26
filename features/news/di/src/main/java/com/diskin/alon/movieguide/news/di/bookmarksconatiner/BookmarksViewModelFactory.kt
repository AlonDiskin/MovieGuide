package com.diskin.alon.movieguide.news.di.bookmarksconatiner

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksFragment
import com.diskin.alon.movieguide.news.presentation.viewmodel.BookmarksViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class BookmarksViewModelFactory @Inject constructor(
    owner: BookmarksFragment,
    private val modelProvider: Provider<Model>,
) : AbstractSavedStateViewModelFactory(owner, owner.arguments) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return BookmarksViewModelImpl(
            modelProvider.get(),
            handle
        ) as T
    }
}