package com.diskin.alon.movieguide.util

import com.diskin.alon.movieguide.news.data.remote.*
import com.diskin.alon.movieguide.news.presentation.viewmodel.MoviesHeadlinesViewModelImpl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray
import org.json.JSONObject

class MockWebServerDispatcher : Dispatcher() {

    companion object {
        private const val INIT_PAGE_SIZE = MoviesHeadlinesViewModelImpl.PAGE_SIZE * 3
        private const val MOVIE_NEWS_STREAM_PATH = "/$FEEDLY_FEED_PATH"
        private const val MOVIE_NEWS_ENTRY_PATH = "/$FEEDLY_ENTRY_PATH/"
        private const val STUB_IMAGES_PATH = "assets/image/stub_image"
        private const val STUB_IMAGE_RES = "assets/image/taco.jpeg"
        const val MOVIE_NEWS_STREAM_RES = "assets/json/feedly_movie_news_stream.json"
        const val MOVIE_NEWS_ENTRY_RES =  "assets/json/feedly_movie_news_entry.json"
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        return when(request.requestUrl.uri().path) {
            MOVIE_NEWS_STREAM_PATH -> {
                // server expect to get a request for non keyed,sized feed
                if (request.requestUrl.query() ==
                    "$FEEDLY_FEED_ID_PARAM=$MOVIES_NEWS_FEED&$FEEDLY_FEED_SIZE_PARAM=${INIT_PAGE_SIZE}"
                ) {
                    MockResponse().setResponseCode(200)
                        .setBody(getMovieStreamJson(request))
                } else {
                    MockResponse().setResponseCode(404)
                }
            }

            MOVIE_NEWS_ENTRY_PATH.plus(getMovieEntryJsonId()) -> MockResponse().setResponseCode(200)
                .setBody(getMovieEntryJson(request))

            STUB_IMAGES_PATH -> MockResponse().setResponseCode(200)
                .setBody(FileReader.readImageIntoBuffer(STUB_IMAGE_RES))

            else -> MockResponse().setResponseCode(404)
        }
    }

    private fun getMovieStreamJson(request: RecordedRequest): String {
        val requestUrl = request.requestUrl
        val imageUrlStub = "${requestUrl.scheme()}://${requestUrl.host()}:${requestUrl.port()}$STUB_IMAGES_PATH"
        val json = FileReader.readStringFromFile(MOVIE_NEWS_STREAM_RES)
        val jsonResponseObject = JSONObject(json)
        val jsonItemsArray = jsonResponseObject.getJSONArray("items")

        for (i in 0 until jsonItemsArray.length()) {
            val jsonItemObject = jsonItemsArray.getJSONObject(i)

            jsonItemObject.getJSONObject("visual").put("url", imageUrlStub)
        }

        return jsonResponseObject.toString()
    }

    private fun getMovieEntryJson(request: RecordedRequest): String {
        val requestUrl = request.requestUrl
        val imageUrlStub = "${requestUrl.scheme()}://${requestUrl.host()}:${requestUrl.port()}$STUB_IMAGES_PATH"
        val json = FileReader.readStringFromFile(MOVIE_NEWS_ENTRY_RES)
        val jsonResponseArray = JSONArray(json)

        jsonResponseArray.getJSONObject(0)!!.getJSONObject("visual").put("url", imageUrlStub)

        return jsonResponseArray.toString()
    }

    private fun getMovieEntryJsonId(): String {
        val json = FileReader.readStringFromFile(MOVIE_NEWS_ENTRY_RES)
        val jsonResponseArray = JSONArray(json)
        val jsonEntry = jsonResponseArray.getJSONObject(0)!!

        return jsonEntry.getString("id")
    }
}