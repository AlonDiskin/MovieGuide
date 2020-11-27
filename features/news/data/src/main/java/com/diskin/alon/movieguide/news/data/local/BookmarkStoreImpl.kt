package com.diskin.alon.movieguide.news.data.local

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import io.reactivex.Observable
import javax.inject.Inject

class BookmarkStoreImpl @Inject constructor(
    private val dao: BookmarkDao
) : BookmarkStore {

    override fun getAll(sorting: BookmarkSorting): Observable<Result<List<Bookmark>>> {
        val bookmarks =  when(sorting) {
            BookmarkSorting.NEWEST -> dao.getHeadlinesDesc()
            BookmarkSorting.OLDEST -> dao.getHeadlinesAsc()
        }

        return bookmarks.map { Result.Success(it) }
    }
}