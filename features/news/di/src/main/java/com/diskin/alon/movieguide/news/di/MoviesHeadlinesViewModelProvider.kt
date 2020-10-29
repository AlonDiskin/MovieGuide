package com.diskin.alon.movieguide.news.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.movieguide.news.appservices.usecase.GetHeadlinesUseCase
import com.diskin.alon.movieguide.news.presentation.viewmodel.MoviesHeadlinesViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class MoviesHeadlinesViewModelProvider @Inject constructor(
    private val useCaseProvider: Provider<GetHeadlinesUseCase>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MoviesHeadlinesViewModelImpl(
            useCaseProvider.get()
        ) as T
    }
}