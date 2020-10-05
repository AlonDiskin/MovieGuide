package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.diskin.alon.movieguide.news.presentation.model.Article

interface ArticleViewModel {

    val article: LiveData<Article>
}
