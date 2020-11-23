package com.diskin.alon.movieguide.news.data.implementation

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.appservices.interfaces.HeadlineRepository
import com.diskin.alon.movieguide.news.data.local.BookmarkedHeadlinesStore
import com.diskin.alon.movieguide.news.data.remote.RemoteHeadlinesStore
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Handles data sources operations to provide movie news headlines.
 */
class HeadlineRepositoryImpl @Inject constructor(
    private val remoteStore: RemoteHeadlinesStore,
    private val bookmarks: BookmarkedHeadlinesStore,
) : HeadlineRepository {

    override fun getPaging(config: PagingConfig): Observable<PagingData<HeadlineEntity>> {
        return remoteStore.getAll(config)
    }

    override fun getBookmarked(sorting: BookmarkSorting): Observable<Result<List<HeadlineEntity>>> {
        return bookmarks.getBookmarked(sorting)
    }
}
