package com.diskin.alon.movieguide.news.di.articleconatiner

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class ArticleViewModelFactory @Inject constructor(
    owner: ArticleActivity,
    private val modelProvider: Provider<Model>
) : AbstractSavedStateViewModelFactory(owner, owner.intent.extras) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return ArticleViewModelImpl(
            modelProvider.get(),
            handle,
        ) as T
    }
}