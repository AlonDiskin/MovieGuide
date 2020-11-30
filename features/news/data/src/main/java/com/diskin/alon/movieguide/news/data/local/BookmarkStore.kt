package com.diskin.alon.movieguide.news.data.local

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import io.reactivex.Observable
import io.reactivex.Single

interface BookmarkStore {

    fun getAll(sorting: BookmarkSorting): Observable<Result<List<Bookmark>>>

    fun contains(id: String): Observable<Result<Boolean>>

    fun add(id: String): Single<Result<Unit>>

    fun remove(ids: List<String>): Single<Result<Unit>>
}