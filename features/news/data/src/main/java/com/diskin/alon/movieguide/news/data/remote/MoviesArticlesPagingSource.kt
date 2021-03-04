package com.diskin.alon.movieguide.news.data.remote

import androidx.paging.rxjava2.RxPagingSource
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyFeedResponse
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Provides a paging of movie news articles from remote api.
 */
class MoviesArticlesPagingSource @Inject constructor(
    private val api: FeedlyApi,
    private val networkErrorHandler: NetworkErrorHandler,
    private val apiArticlesMapper: Mapper<FeedlyEntryResponse, ArticleEntity>,
    private val lastReadArticleStore: LastReadArticleStore
) : RxPagingSource<String,ArticleEntity>() {

    override fun loadSingle(params: LoadParams<String>): Single<LoadResult<String, ArticleEntity>> {
        // Compose api call based on load type
        val apiCall = when(params) {
            is LoadParams.Refresh -> api.getFeedItems(MOVIES_NEWS_FEED,params.loadSize)

            // Handle LoadParams.Append and LoadParams.Prepend(not supported yet)
            // params.key is a non nullable property is bot types
            else -> api.getFeedItemsPage(MOVIES_NEWS_FEED,params.key!!,params.loadSize)
        }

        return apiCall
            .subscribeOn(Schedulers.io())
            .map(this::toLoadResult)
            .onErrorReturn{ toLoadResultError(it) }
    }

    private fun toLoadResult(response: FeedlyFeedResponse): LoadResult<String, ArticleEntity> {
        lastReadArticleStore.putLastDate(response.items.first().published)
        return LoadResult.Page(
            response.items.map(apiArticlesMapper::map),
            null,  // Only paging forward.
            response.continuation,
            LoadResult.Page.COUNT_UNDEFINED,
            LoadResult.Page.COUNT_UNDEFINED
        )
    }

    private fun toLoadResultError(e: Throwable): LoadResult<String,ArticleEntity> {
        val networkError = networkErrorHandler.handle(e)
        val throwable = Throwable(networkError.description)

        return LoadResult.Error(throwable)
    }
}