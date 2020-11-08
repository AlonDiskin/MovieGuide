package com.diskin.alon.movieguide.news.data.implementation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.rxjava2.observable
import com.diskin.alon.movieguide.news.appservices.interfaces.HeadlineRepository
import com.diskin.alon.movieguide.news.data.local.MoviesHeadlinesPagingSource
import com.diskin.alon.movieguide.news.data.remote.FeedlyApi
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import javax.inject.Inject

class HeadlineRepositoryImpl @Inject constructor(
    private val api: FeedlyApi,
    private val networkErrorHandler: NetworkErrorHandler
) : HeadlineRepository {

    override fun getPaging(config: PagingConfig) =
        Pager(config)
        { MoviesHeadlinesPagingSource(api,networkErrorHandler) }
            .observable
}
