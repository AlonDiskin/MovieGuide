package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.diskin.alon.movieguide.news.presentation.model.NewsHeadline

/**
 * View model contract for movie headlines screen.
 */
interface MoviesHeadlinesViewModel {

    val headlines: LiveData<PagingData<NewsHeadline>>
}
