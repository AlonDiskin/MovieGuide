package com.diskin.alon.movieguide.news.data.local

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import io.reactivex.Observable

interface BookmarkedHeadlinesStore {

    fun getBookmarked(sorting: BookmarkSorting): Observable<Result<List<HeadlineEntity>>>
}