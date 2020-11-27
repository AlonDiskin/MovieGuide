package com.diskin.alon.movieguide.news.data.implementation

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.data.local.BookmarkStore
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import com.diskin.alon.movieguide.news.data.remote.RemoteArticleStore
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable
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
        return bookmarkedStore.getAll(sorting)
            .switchMap(this::getRemoteBookmarkedArticles)
    }

    private fun getRemoteBookmarkedArticles(result: Result<List<Bookmark>>): Observable<Result<List<ArticleEntity>>> {
        return when(result) {
            is Result.Success -> {
                val ids = result.data.map { it.articleId }
                remoteStore.getAll(*ids.toTypedArray())
            }
            is Result.Error -> Observable.just(Result.Error(result.error))
        }
    }
}