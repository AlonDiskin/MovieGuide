package com.diskin.alon.movieguide.news.data.remote

/**
 * Feedly api response for a GET request for a rss feed contents.
 */
data class FeedlyFeedResponse(val items: List<FeedEntry>,
                              val continuation: String?) {

    data class FeedEntry(val id: String,
                         val title: String,
                         val published: Long,
                         val visual: Visual?,
                         val originId: String
    )

    data class Visual(val url: String)
}