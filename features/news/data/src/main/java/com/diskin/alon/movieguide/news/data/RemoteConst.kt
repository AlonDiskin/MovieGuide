package com.diskin.alon.movieguide.news.data

// Feedly api url
const val FEEDLY_BASE = "https://cloud.feedly.com/v3/"
// Feedly api movie news stream url
const val MOVIES_NEWS_FEED = "feed/http://www.collider.com/rss.asp"
const val FEEDLY_FEED_PATH = "streams/contents"
const val FEEDLY_FEED_ID_PARAM = "streamId"
const val FEEDLY_FEED_SIZE_PARAM = "count"
const val FEEDLY_PAGE_KEY_PARAM = "continuation"

// Network error messages
const val ERR_DEVICE_NETWORK = "Network error,check device connectivity"
const val ERR_API_SERVER = "Server currently unavailable"
const val ERR_UNKNOWN_NETWORK = "Unknown network error"