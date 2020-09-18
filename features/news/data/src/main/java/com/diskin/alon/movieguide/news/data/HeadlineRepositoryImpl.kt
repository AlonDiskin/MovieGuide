package com.diskin.alon.movieguide.news.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.rxjava2.observable
import com.diskin.alon.movieguide.news.appservices.HeadlineRepository
import javax.inject.Inject

class HeadlineRepositoryImpl @Inject constructor(
    private val api: FeedlyApi
) : HeadlineRepository {

    override fun getPaging(config: PagingConfig) =
        Pager(config)
        { MoviesHeadlinesPagingSource(api) }
            .observable

}
