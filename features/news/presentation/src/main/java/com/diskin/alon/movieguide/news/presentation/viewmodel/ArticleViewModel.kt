package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.news.presentation.data.Article

interface ArticleViewModel {

    val article: LiveData<Article>

    val error: LiveData<ErrorViewData>

    val update: LiveData<UpdateViewData>

    fun bookmark()

    fun unBookmark()
}
