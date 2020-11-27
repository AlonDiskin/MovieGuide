package com.diskin.alon.movieguide.util

import com.diskin.alon.movieguide.news.data.remote.*
import com.diskin.alon.movieguide.news.presentation.viewmodel.HeadlinesViewModelImpl
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray
import org.json.JSONObject

class MockWebServerDispatcher : Dispatcher() {

    companion object {
        private const val INIT_PAGE_SIZE = HeadlinesViewModelImpl.PAGE_SIZE * 3
        private const val MOVIE_NEWS_STREAM_PATH = "/$FEEDLY_FEED_PATH"
        private const val MOVIE_NEWS_ENTRY_PATH = "/$FEEDLY_ENTRY_PATH/"
        private const val STUB_IMAGES_PATH = "assets/image/stub_image"
        private const val STUB_IMAGE_RES = "assets/image/taco.jpeg"
        const val MOVIE_NEWS_STREAM_RES = "assets/json/feedly_movie_news_page.json"
        const val MOVIE_NEWS_ENTRY_RES =  "assets/json/feedly_movie_news_entry.json"
        const val MOVIES_POPULAR_RES = "assets/json/themoviedb_movies_sorted_by_popularity.json"
        const val MOVIE_DETAIL_RES = "assets/json/themoviedb_movie_detail.json"
        const val MOVIE_TRAILERS_RES = "assets/json/themoviedb_movie_trailers.json"
        const val MOVIES_POPULAR_PATH = "/3/discover/movie"
        private const val MOVIE_DETAIL_PATH = "/3/movie/724989"
        private const val MOVIE_TRAILERS_PATH = "/3/movie/724989/videos"
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

            MOVIES_POPULAR_PATH -> {
                return if (
                    request.requestUrl.queryParameter("page") == "1" &&
                    request.requestUrl.queryParameter("include_video") == "false" &&
                    request.requestUrl.queryParameter("include_adult") == "false" &&
                    request.requestUrl.queryParameter("sort_by") == "popularity.desc" &&
                    request.requestUrl.queryParameter("language") == "en" &&
                    request.requestUrl.queryParameter("api_key") == BuildConfig.MOVIE_DB_API_KEY
                ) {
                    MockResponse()
                        .setBody(FileReader.readStringFromFile(MOVIES_POPULAR_RES))
                        .setResponseCode(200)
                } else {
                    MockResponse().setResponseCode(404)
                }
            }

            MOVIE_DETAIL_PATH -> {
                return if (
                    request.requestUrl.queryParameter("api_key") ==
                    BuildConfig.MOVIE_DB_API_KEY
                ) {
                    MockResponse()
                        .setBody(FileReader.readStringFromFile(MOVIE_DETAIL_RES))
                        .setResponseCode(200)
                } else{
                    MockResponse().setResponseCode(404)
                }
            }

            MOVIE_TRAILERS_PATH -> {
                return if (
                    request.requestUrl.queryParameter("api_key") ==
                    BuildConfig.MOVIE_DB_API_KEY
                ) {
                    MockResponse()
                        .setBody(FileReader.readStringFromFile(MOVIE_TRAILERS_RES))
                        .setResponseCode(200)
                } else{
                    MockResponse().setResponseCode(404)
                }
            }

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