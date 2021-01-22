package com.diskin.alon.movieguide.news.di.articleconatiner

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.util.ArticleModelDispatcher
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModelImpl
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject
import javax.inject.Provider

class ArticleViewModelFactory @Inject constructor(
    @ActivityContext context: Context,
    @ArticleModelDispatcher private val model: Provider<Model>
) : AbstractSavedStateViewModelFactory(context as ArticleActivity, context.intent.extras) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return ArticleViewModelImpl(
            model.get(),
            handle,
        ) as T
    }
}