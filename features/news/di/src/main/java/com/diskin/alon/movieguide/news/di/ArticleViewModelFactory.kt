package com.diskin.alon.movieguide.news.di

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.movieguide.news.appservices.usecase.GetArticleUseCase
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class ArticleViewModelFactory @Inject constructor(
    owner: ArticleActivity,
    private val useCaseProvider: Provider<GetArticleUseCase>
) : AbstractSavedStateViewModelFactory(owner, owner.intent.extras) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return ArticleViewModelImpl(useCaseProvider.get(),handle) as T
    }
}