package com.diskin.alon.movieguide.news.di.headlinescontainer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.news.presentation.util.HeadlinesModelDispatcher
import com.diskin.alon.movieguide.news.presentation.viewmodel.HeadlinesViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class HeadlinesViewModelFactory @Inject constructor(
    @HeadlinesModelDispatcher private val modelProvider: Provider<Model>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HeadlinesViewModelImpl(
            modelProvider.get()
        ) as T
    }
}