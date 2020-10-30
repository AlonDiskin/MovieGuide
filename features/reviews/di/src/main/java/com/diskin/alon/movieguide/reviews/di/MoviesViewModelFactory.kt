package com.diskin.alon.movieguide.reviews.di

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.movieguide.reviews.appservices.usecase.GetSortedMoviesUseCase
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviePagingMapper
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class MoviesViewModelFactory @Inject constructor(
    owner: MoviesFragment,
    private val useCaseProvider: Provider<GetSortedMoviesUseCase>,
    private val moviePagingMapperProvider: Provider<MoviePagingMapper>
) : AbstractSavedStateViewModelFactory(owner, owner.arguments) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return MoviesViewModelImpl(
            useCaseProvider.get(),
            moviePagingMapperProvider.get(),
            handle
        ) as T
    }
}