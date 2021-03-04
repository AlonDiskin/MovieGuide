package com.diskin.alon.movieguide.news.infrastructure

import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyFeedResponse
import java.util.*

fun createFeedlyResponseForRecentArticle(): FeedlyFeedResponse {
    return FeedlyFeedResponse(
        arrayListOf(
            FeedlyEntryResponse(
                "",
                "",
                "",
                Calendar.getInstance().timeInMillis,
                null,
                "",
                null
            )
        ),
        ""
    )
}