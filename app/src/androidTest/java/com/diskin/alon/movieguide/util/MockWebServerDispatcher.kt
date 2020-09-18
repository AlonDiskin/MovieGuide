package com.diskin.alon.movieguide.util

import com.diskin.alon.movieguide.news.data.FEEDLY_FEED_ID_PARAM
import com.diskin.alon.movieguide.news.data.FEEDLY_FEED_PATH
import com.diskin.alon.movieguide.news.data.FEEDLY_FEED_SIZE_PARAM
import com.diskin.alon.movieguide.news.data.MOVIES_NEWS_FEED
import com.diskin.alon.movieguide.news.presentation.MoviesHeadlinesViewModelImpl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject

class MockWebServerDispatcher : Dispatcher() {

    companion object {
        private const val INIT_PAGE_SIZE = MoviesHeadlinesViewModelImpl.PAGE_SIZE * 3
        private const val MOVIES_HEADLINES_PAGE_PATH = "/$FEEDLY_FEED_PATH"
        private const val STUB_IMAGES_PATH = "/stub_image"
        const val MOVIE_HEADLINES_JSON_RES = "assets/test_movie_headlines.json"
        const val MOVIE_HEADLINES_IMAGE_RES = "assets/taco.jpeg"
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        return when(request.requestUrl.url().path) {
            MOVIES_HEADLINES_PAGE_PATH -> {
                // server expect to get a request for non keyed,sized feed
                if (request.requestUrl.query() ==
                    "$FEEDLY_FEED_ID_PARAM=$MOVIES_NEWS_FEED&$FEEDLY_FEED_SIZE_PARAM=${INIT_PAGE_SIZE}"
                ) {
                    MockResponse().setResponseCode(200)
                        .setBody(getMovieHeadlinesJson(request))
                } else {
                    MockResponse().setResponseCode(404)
                }
            }

            STUB_IMAGES_PATH -> {
                MockResponse().setResponseCode(200)
                    .setBody(FileReader.readImageIntoBuffer(MOVIE_HEADLINES_IMAGE_RES))
            }

            else -> MockResponse().setResponseCode(404)
        }
    }

    private fun getMovieHeadlinesJson(request: RecordedRequest): String {
        val requestUrl = request.requestUrl
        val imageUrlStub = "${requestUrl.scheme()}://${requestUrl.host()}:${requestUrl.port()}$STUB_IMAGES_PATH"
        val json = FileReader.readStringFromFile(MOVIE_HEADLINES_JSON_RES)
        val jsonResponseObject = JSONObject(json)
        val jsonItemsArray = jsonResponseObject.getJSONArray("items")

        for (i in 0 until jsonItemsArray.length()) {
            val jsonItemObject = jsonItemsArray.getJSONObject(i)

            jsonItemObject.getJSONObject("visual").put("url", imageUrlStub)
        }

        return jsonResponseObject.toString()
    }
}