package com.diskin.alon.movieguide.news.data.local

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import io.reactivex.Observable

interface BookmarkStore {

    fun getAll(sorting: BookmarkSorting): Observable<Result<List<Bookmark>>>
}