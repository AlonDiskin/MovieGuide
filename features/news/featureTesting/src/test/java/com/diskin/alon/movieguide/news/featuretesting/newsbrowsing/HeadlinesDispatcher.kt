package com.diskin.alon.movieguide.news.featuretesting.newsbrowsing

import com.diskin.alon.movieguide.common.featuretesting.getJsonFromResource
import com.diskin.alon.movieguide.news.data.remote.*
import com.diskin.alon.movieguide.news.presentation.viewmodel.HeadlinesViewModelImpl.Companion.PAGE_SIZE
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class HeadlinesDispatcher : Dispatcher() {

    companion object {
        private const val INIT_PAGE_SIZE = PAGE_SIZE * 3
        private const val PAGE_KEY = "174d587cb4b:149a2e0:5d3e1c98"
        private const val REFRESH_PAGE_KEY = "174daad7957:4e7e98:5d3e1c98"
        private const val INITIAL_PAGE_PATH = "/$FEEDLY_FEED_PATH?" +
                "$FEEDLY_FEED_ID_PARAM=$MOVIES_NEWS_FEED&" +
                "$FEEDLY_FEED_SIZE_PARAM=$INIT_PAGE_SIZE"
        private const val LAST_PAGE_PATH = "/$FEEDLY_FEED_PATH?" +
                "$FEEDLY_FEED_ID_PARAM=$MOVIES_NEWS_FEED&" +
                "$FEEDLY_PAGE_KEY_PARAM=$PAGE_KEY&" +
                "$FEEDLY_FEED_SIZE_PARAM=$PAGE_SIZE"
        private const val REFRESH_LAST_PAGE_PATH = "/$FEEDLY_FEED_PATH?" +
                "$FEEDLY_FEED_ID_PARAM=$MOVIES_NEWS_FEED&" +
                "$FEEDLY_PAGE_KEY_PARAM=$REFRESH_PAGE_KEY&" +
                "$FEEDLY_FEED_SIZE_PARAM=$PAGE_SIZE"
    }

    private var pageSources = Pair("json/feed_init_page.json","json/feed_last_page.json")

    override fun dispatch(request: RecordedRequest): MockResponse {
        val decodedPath = "${request.requestUrl.url().path}?${request.requestUrl.query()}"
        return when(decodedPath) {
            INITIAL_PAGE_PATH -> {
                MockResponse().setResponseCode(200)
                    .setBody(getJsonFromResource(pageSources.first))
            }

            LAST_PAGE_PATH -> {
                MockResponse().setResponseCode(200)
                    .setBody(getJsonFromResource(pageSources.second))
            }

            REFRESH_LAST_PAGE_PATH -> {
                MockResponse().setResponseCode(200)
                    .setBody(getJsonFromResource(pageSources.second))
            }

            else -> throw IllegalArgumentException("unexpected request")
        }
    }

    fun refresh() {
        pageSources = Pair("json/refresh_feed_init_page.json", "json/refresh_feed_last_page.json")
    }
}