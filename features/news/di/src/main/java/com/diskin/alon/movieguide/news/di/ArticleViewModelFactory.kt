package com.diskin.alon.movieguide.news.di

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.movieguide.news.appservices.usecase.GetArticleUseCase
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleMapper
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModelImpl
import javax.inject.Inject
import javax.inject.Provider

class ArticleViewModelFactory @Inject constructor(
    private val owner: ArticleActivity,
    private val useCaseProvider: Provider<GetArticleUseCase>,
    private val articleMapperProvider: Provider<ArticleMapper>
) : AbstractSavedStateViewModelFactory(owner, owner.intent.extras) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return ArticleViewModelImpl(
            useCaseProvider.get(),
            articleMapperProvider.get(),
            handle,
            owner.resources
        ) as T
    }
}