package com.diskin.alon.movieguide.news.data.remote

import com.diskin.alon.movieguide.news.data.remote.data.FeedlyArticleId
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyFeedResponse
import io.reactivex.Single
import retrofit2.http.*

/**
 * Feedly REST api client interface, that loads data from [http://cloud.feedly.com/v3/].
 */
interface FeedlyApi {

    @GET(FEEDLY_FEED_PATH)
    fun getFeedItems(
        @Query(FEEDLY_FEED_ID_PARAM) streamId: String,
        @Query(FEEDLY_FEED_SIZE_PARAM) count: Int
    ): Single<FeedlyFeedResponse>

    @GET(FEEDLY_FEED_PATH)
    fun getFeedItemsPage(
        @Query(FEEDLY_FEED_ID_PARAM) streamId: String,
        @Query(FEEDLY_PAGE_KEY_PARAM) continuation: String,
        @Query(FEEDLY_FEED_SIZE_PARAM) count: Int
    ): Single<FeedlyFeedResponse>

    @GET("$FEEDLY_ENTRY_PATH/{entryId}")
    fun getEntry(@Path(value = "entryId", encoded = true) entryId: String): Single<List<FeedlyEntryResponse>>

    @POST(FEEDLY_ENTRIES_PATH)
    fun getEntries(@Body ids: List<FeedlyArticleId>): Single<List<FeedlyEntryResponse>>
}
