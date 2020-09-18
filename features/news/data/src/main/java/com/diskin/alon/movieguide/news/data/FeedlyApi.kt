package com.diskin.alon.movieguide.news.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

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
}
