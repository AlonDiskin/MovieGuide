package com.diskin.alon.movieguide.news.appservices.interfaces

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import io.reactivex.Observable

/**
 * Repository contract for [HeadlineEntity]
 */
interface HeadlineRepository {

    fun getPaging(config: PagingConfig): Observable<PagingData<HeadlineEntity>>

    fun getBookmarked(sorting: BookmarkSorting): Observable<Result<List<HeadlineEntity>>>
}