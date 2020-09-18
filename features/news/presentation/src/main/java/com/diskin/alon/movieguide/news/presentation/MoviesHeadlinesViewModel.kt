package com.diskin.alon.movieguide.news.presentation

import androidx.lifecycle.LiveData
import androidx.paging.PagingData

/**
 * View model contract for movie headlines screen.
 */
interface MoviesHeadlinesViewModel {

    val headlines: LiveData<PagingData<NewsHeadline>>
}
