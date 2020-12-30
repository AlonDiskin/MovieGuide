package com.diskin.alon.movieguide.reviews.di.reviewcontainer

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MovieReviewViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class MovieReviewViewModelFactory @Inject constructor(
    owner: MovieReviewActivity,
    private val modelProvider: Provider<Model>,
) : AbstractSavedStateViewModelFactory(owner, owner.intent.extras) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return MovieReviewViewModelImpl(
            modelProvider.get(),
            handle
        ) as T
    }
}