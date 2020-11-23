package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.diskin.alon.movieguide.news.presentation.data.Headline

/**
 * View model contract for movie headlines screen.
 */
interface HeadlinesViewModel {

    val headlines: LiveData<PagingData<Headline>>
}
