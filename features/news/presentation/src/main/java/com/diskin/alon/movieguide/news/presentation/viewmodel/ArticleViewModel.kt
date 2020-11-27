package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.diskin.alon.movieguide.common.presentation.LoadState
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.news.presentation.data.Article

interface ArticleViewModel {

    val article: LiveData<ViewData<Article>>
}
