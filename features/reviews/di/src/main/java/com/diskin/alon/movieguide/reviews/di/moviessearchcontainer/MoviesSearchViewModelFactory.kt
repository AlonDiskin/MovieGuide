package com.diskin.alon.movieguide.reviews.di.moviessearchcontainer

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesSearchFragment
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesSearchViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class MoviesSearchViewModelFactory @Inject constructor(
    owner: MoviesSearchFragment,
    private val modelProvider: Provider<Model>,
) : AbstractSavedStateViewModelFactory(owner, owner.arguments) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return MoviesSearchViewModelImpl(
            modelProvider.get(),
            handle
        ) as T
    }
}