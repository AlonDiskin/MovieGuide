package com.diskin.alon.movieguide.news.data.implementation

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.toData
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.data.local.BookmarkStore
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import com.diskin.alon.movieguide.news.data.remote.RemoteArticleStore
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

/**
 * Handles data sources operations to provide movie news articles.
 */
class ArticleRepositoryImpl @Inject constructor(
    private val remoteStore: RemoteArticleStore,
    private val bookmarkedStore: BookmarkStore
) : ArticleRepository {

    override fun get(id: String): Observable<Result<ArticleEntity>> {
        return remoteStore.getArticle(id)
    }

    override fun getPaging(config: PagingConfig): Observable<PagingData<ArticleEntity>> {
        return remoteStore.getPaging(config)
    }

    override fun getBookmarked(sorting: BookmarkSorting): Observable<Result<List<ArticleEntity>>> {
        return bookmarkedStore.getAll(sorting).toData()
            .concatMap(this::getRemoteBookmarkedArticles)
    }

    override fun bookmark(id: String): Single<Result<Unit>> {
        return bookmarkedStore.add(id)
    }

    override fun unBookmark(ids: List<String>): Single<Result<Unit>> {
        return bookmarkedStore.remove(ids)
    }

    override fun isBookmarked(id: String): Observable<Result<Boolean>> {
        return bookmarkedStore.contains(id)
    }

    private fun getRemoteBookmarkedArticles(bookmarks: List<Bookmark>): Observable<Result<List<ArticleEntity>>> {
        val ids = bookmarks.map { it.articleId }
        return remoteStore.getAll(*ids.toTypedArray())
    }
}