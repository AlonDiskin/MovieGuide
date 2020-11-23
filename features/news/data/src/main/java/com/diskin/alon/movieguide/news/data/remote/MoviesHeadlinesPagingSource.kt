package com.diskin.alon.movieguide.news.data.remote

import androidx.paging.rxjava2.RxPagingSource
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Provides paged [HeadlineEntity] from remote api.
 */
class MoviesHeadlinesPagingSource @Inject constructor(
    private val api: FeedlyApi,
    private val networkErrorHandler: NetworkErrorHandler
) : RxPagingSource<String,HeadlineEntity>() {

    override fun loadSingle(params: LoadParams<String>): Single<LoadResult<String, HeadlineEntity>> {
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

    /**
     * Maps remote api [FeedlyFeedResponse] to a [LoadResult].
     */
    private fun toLoadResult(response: FeedlyFeedResponse): LoadResult<String, HeadlineEntity> {
        return LoadResult.Page(
            response.items.map { entry ->
                HeadlineEntity(
                    entry.id,
                    entry.title,
                    entry.published,
                    entry.visual?.url ?: "",
                    entry.originId
                )
            },
            null,  // Only paging forward.
            response.continuation,
            LoadResult.Page.COUNT_UNDEFINED,
            LoadResult.Page.COUNT_UNDEFINED
        )
    }

    /**
     * Maps remote api errors, to [Throwable] containing description message about
     */
    private fun toLoadResultError(e: Throwable): LoadResult<String,HeadlineEntity> {
        val networkError = networkErrorHandler.handle(e)
        val throwable = Throwable(networkError.cause)

        return LoadResult.Error(throwable)
    }
}