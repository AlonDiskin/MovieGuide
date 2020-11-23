package com.diskin.alon.movieguide.news.data.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.observable
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Handle data operations to provide movie news headlines from remote sources.
 */
class RemoteHeadlinesStoreImpl @Inject constructor(
    private val api: FeedlyApi,
    private val networkErrorHandler: NetworkErrorHandler
) : RemoteHeadlinesStore {

    override fun getAll(config: PagingConfig): Observable<PagingData<HeadlineEntity>> {
        return Pager(config)
        { MoviesHeadlinesPagingSource(api,networkErrorHandler) }
            .observable
    }
}