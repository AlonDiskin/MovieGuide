package com.diskin.alon.movieguide.news.data.local

import androidx.paging.rxjava2.RxPagingSource
import com.diskin.alon.movieguide.news.data.remote.*
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException
import java.util.*

/**
 * Provides paged [HeadlineEntity] from remote api.
 */
class MoviesHeadlinesPagingSource(
    private val api: FeedlyApi
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
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = entry.published

                HeadlineEntity(
                    entry.id,
                    entry.title,
                    calendar,
                    entry.visual.url,
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
        return when (e) {
            // Retrofit calls that return the body type throw either IOException for
            // network failures, or HttpException for any non-2xx HTTP status codes.
            // This code reports all errors to the UI
            is IOException -> LoadResult.Error(Throwable(ERR_DEVICE_NETWORK))
            is HttpException -> LoadResult.Error(Throwable(ERR_API_SERVER))
            else -> LoadResult.Error(Throwable(ERR_UNKNOWN_NETWORK))
        }
    }
}