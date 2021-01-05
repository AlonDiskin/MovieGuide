package com.diskin.alon.movieguide.news.data.local

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.toResult
import com.diskin.alon.movieguide.common.appservices.toSingleResult
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkStoreImpl @Inject constructor(
    private val dao: BookmarkDao,
    private val errorHandler: StorageErrorHandler
) : BookmarkStore {

    override fun getAll(sorting: BookmarkSorting): Observable<Result<List<Bookmark>>> {
        val bookmarks =  when(sorting) {
            BookmarkSorting.NEWEST -> dao.getAllDesc()
            BookmarkSorting.OLDEST -> dao.getAll()
        }

        return bookmarks
            .toResult(errorHandler::handle)
    }

    override fun contains(id: String): Observable<Result<Boolean>> {
        return dao.getAll()
            .map { bookmarks -> bookmarks.any { it.articleId == id } }
            .toResult(errorHandler::handle)
    }

    override fun add(id: String): Single<Result<Unit>> {
        return dao.insert(Bookmark(id))
            .subscribeOn(Schedulers.io())
            .toSingleDefault(Unit)
            .toSingleResult(errorHandler::handle)
    }

    override fun remove(ids: List<String>): Single<Result<Unit>> {
        return Single.fromCallable { dao.deleteAllByArticleId(ids) }
            .subscribeOn(Schedulers.io())
            .toSingleResult(errorHandler::handle)
    }
}