package com.diskin.alon.movieguide.news.data.remote.data

/**
 * Feedly api response for a GET request for a rss feed contents.
 */
data class FeedlyFeedResponse(val items: List<FeedlyEntryResponse>,
                              val continuation: String?)