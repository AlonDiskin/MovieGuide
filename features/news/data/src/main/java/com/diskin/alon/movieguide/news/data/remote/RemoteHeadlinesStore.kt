package com.diskin.alon.movieguide.news.data.remote

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import io.reactivex.Observable

interface RemoteHeadlinesStore {

    fun getAll(config: PagingConfig): Observable<PagingData<HeadlineEntity>>
}