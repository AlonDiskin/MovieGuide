package com.diskin.alon.movieguide.news.infrastructure

import com.diskin.alon.movieguide.news.data.remote.FeedlyApi
import com.diskin.alon.movieguide.news.data.remote.MOVIES_NEWS_FEED
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class RemoteRecentDateProviderImpl @Inject constructor(
    private val feedlyApi: FeedlyApi
) : RemoteRecentDateProvider {

    override fun getDate(): Single<Date> {
        return feedlyApi.getFeedItems(MOVIES_NEWS_FEED,1)
            .map { response ->
                val entry =  response.items.first()
                Date(entry.published)
            }
    }
}