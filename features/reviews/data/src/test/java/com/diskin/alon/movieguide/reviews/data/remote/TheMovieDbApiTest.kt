package com.diskin.alon.movieguide.reviews.data.remote

import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.data.remote.data.MovieDetailResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.TrailersResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse.*
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * [TheMovieDbApi] integration test class.
 */
class TheMovieDbApiTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Fix MockWebServer 'No password supplied for PKCS#12 KeyStore' bug on CI machine
            System.setProperty("javax.net.ssl.trustStore", "NONE")
        }
    }

    private lateinit var api: TheMovieDbApi
    private val server = MockWebServer()

    @Before
    fun setUp() {
        // Init api client
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
            .create(TheMovieDbApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun getMoviesByPopularityFromRemoteAndMapResponse() {
        // Text case fixture
        val dispatcher = object : Dispatcher() {
            val pageNum = 1
            val moviesResourcePath = "themoviedb_movies_sorted_by_popularity.json"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = "/3/discover/movie"

                return when(request.requestUrl.uri().path) {
                    path -> {
                        return if (
                            request.requestUrl.queryParameter("page") == "1" &&
                            request.requestUrl.queryParameter("include_video") == "false" &&
                            request.requestUrl.queryParameter("include_adult") == "false" &&
                            request.requestUrl.queryParameter("sort_by") == "popularity.desc" &&
                            request.requestUrl.queryParameter("language") == "en" &&
                            request.requestUrl.queryParameter("api_key") == BuildConfig.MOVIE_DB_API_KEY
                        ) {
                            MockResponse()
                                .setBody(getJsonFromResource(moviesResourcePath))
                                .setResponseCode(200)
                        } else {
                            MockResponse().setResponseCode(404)
                        }
                    }

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Given remote api server is running and api client is initialized
        server.setDispatcher(dispatcher)

        // When api sends get request to fetch movies sorted by popularity
        val testObserver = api.getByPopularity(dispatcher.pageNum).test()

        // Then api should map server response to expected data model
        val testWebJson = getJsonFromResource(dispatcher.moviesResourcePath)
        val expectedApiResponse = parseMoviesResponseFromJson(testWebJson)
        testObserver.assertValue { it == expectedApiResponse }
    }

    @Test
    fun getMoviesByReleaseDateFromRemoteAndMapResponse() {
        // Text case fixture
        val dispatcher = object : Dispatcher() {
            val pageNum = 1
            val moviesResourcePath = "themoviedb_movies_sorted_by_date.json"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = "/3/discover/movie"

                return when(request.requestUrl.uri().path) {
                    path -> {
                        return if (
                            request.requestUrl.queryParameter("page") == "1" &&
                            request.requestUrl.queryParameter("include_video") == "false" &&
                            request.requestUrl.queryParameter("include_adult") == "false" &&
                            request.requestUrl.queryParameter("sort_by") == "release_date.desc" &&
                            request.requestUrl.queryParameter("language") == "en" &&
                            request.requestUrl.queryParameter("api_key") == BuildConfig.MOVIE_DB_API_KEY
                        ) {
                            MockResponse()
                                .setBody(getJsonFromResource(moviesResourcePath))
                                .setResponseCode(200)
                        } else {
                            MockResponse().setResponseCode(404)
                        }
                    }

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Given remote api server is running and api client is initialized
        server.setDispatcher(dispatcher)

        // When api sends GET request to fetch movies sorted by release date
        val testObserver = api.getByReleaseDate(dispatcher.pageNum).test()

        // Then api should map server response to expected data model
        val testWebJson = getJsonFromResource(dispatcher.moviesResourcePath)
        val expectedApiResponse = parseMoviesResponseFromJson(testWebJson)
        testObserver.assertValue { it == expectedApiResponse }
    }

    @Test
    fun getMoviesByRatingFromRemoteAndMapResponse() {
        // Text case fixture
        val dispatcher = object : Dispatcher() {
            val pageNum = 1
            val moviesResourcePath = "themoviedb_movies_sorted_by_rating.json"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = "/3/discover/movie"

                return when(request.requestUrl.uri().path) {
                    path -> {
                        return if (
                            request.requestUrl.queryParameter("page") == "1" &&
                            request.requestUrl.queryParameter("include_video") == "false" &&
                            request.requestUrl.queryParameter("include_adult") == "false" &&
                            request.requestUrl.queryParameter("sort_by") == "vote_average.desc" &&
                            request.requestUrl.queryParameter("language") == "en" &&
                            request.requestUrl.queryParameter("api_key") == BuildConfig.MOVIE_DB_API_KEY
                        ) {
                            MockResponse()
                                .setBody(getJsonFromResource(moviesResourcePath))
                                .setResponseCode(200)
                        } else {
                            MockResponse().setResponseCode(404)
                        }
                    }

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Given remote api server is running and api client is initialized
        server.setDispatcher(dispatcher)

        // When api sends GET request to fetch movies sorted by rating
        val testObserver = api.getByRating(dispatcher.pageNum).test()

        // Then api should map server response to expected data model
        val testWebJson = getJsonFromResource(dispatcher.moviesResourcePath)
        val expectedApiResponse = parseMoviesResponseFromJson(testWebJson)
        testObserver.assertValue { it == expectedApiResponse }
    }

    @Test
    fun getMovieDetailFromRemoteAndMapResponse() {
        // Test case fixture
        val dispatcher = object : Dispatcher() {
            val movieId = 1
            val movieDetailResourcePath = "themoviedb_movie_detail.json"
            val apiKey = "key"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = "/3/movie/$movieId?api_key=$apiKey"

                return when(request.path) {
                    path -> MockResponse()
                        .setBody(getJsonFromResource(movieDetailResourcePath))
                        .setResponseCode(200)

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Given remote api server is running and api client is initialized
        server.setDispatcher(dispatcher)

        // When api send GET request to fetch existing movie detail data
        // from remote server,with a valid api key
        val testObserver = api.getMovieDetail(
            dispatcher.movieId,dispatcher.apiKey).test()

        // Then api client should map remote server response
        val testWebJson = getJsonFromResource(dispatcher.movieDetailResourcePath)
        val expectedResponse = parseMovieDetailResponseFromJson(testWebJson)
        testObserver.assertValue { it == expectedResponse }
    }

    @Test
    fun getMovieFromRemoteAndMapResponse() {
        // Test case fixture
        val dispatcher = object : Dispatcher() {
            val movieId = 1
            val movieDetailResourcePath = "themoviedb_movie_detail.json"
            val apiKey = "key"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = "/3/movie/$movieId?api_key=$apiKey"

                return when(request.path) {
                    path -> MockResponse()
                        .setBody(getJsonFromResource(movieDetailResourcePath))
                        .setResponseCode(200)

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Given remote api server is running and api client is initialized
        server.setDispatcher(dispatcher)

        // When api send GET request to fetch existing movie data
        // from remote server,with a valid api key
        val testObserver = api.getMovie(
            dispatcher.movieId,dispatcher.apiKey).test()

        // Then api client should map remote server response
        val testWebJson = getJsonFromResource(dispatcher.movieDetailResourcePath)
        val expectedResponse = parseMovieResponseFromJson(testWebJson)
        testObserver.assertValue { it == expectedResponse }
    }

    @Test
    fun getMovieTrailersFromRemoteAndMapResponse() {
        // Test case fixture
        val dispatcher = object : Dispatcher() {
            val movieId = 1
            val movieTrailersResourcePath = "themoviedb_movie_trailers.json"
            val apiKey = "key"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = "/3/movie/$movieId/videos?api_key=$apiKey"

                return when(request.path) {
                    path -> MockResponse()
                        .setBody(getJsonFromResource(movieTrailersResourcePath))
                        .setResponseCode(200)

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Given remote api server is running and api client is initialized
        server.setDispatcher(dispatcher)

        // When api send GET request to fetch existing movie trailers data
        // from remote server,with a valid api key
        val testObserver = api.getTrailers(
            dispatcher.movieId,dispatcher.apiKey).test()

        // Then api client should map remote server response
        val testWebJson = getJsonFromResource(dispatcher.movieTrailersResourcePath)
        val expectedResponse = parseMovieTrailersResponseFromJson(testWebJson)

        testObserver.assertValue { it == expectedResponse }
    }

    private fun getJsonFromResource(resource: String): String {
        val topLevelClass = object : Any() {}.javaClass.enclosingClass!!
        val jsonResource = topLevelClass.classLoader!! // javaClass.classLoader
            .getResource(resource)

        return File(jsonResource.toURI()).readText()
    }

    private fun parseMoviesResponseFromJson(json: String): MoviesResponse {
        val gson = Gson()
        return gson.fromJson(json, MoviesResponse::class.java)
    }

    private fun parseMovieDetailResponseFromJson(json: String): MovieDetailResponse {
        val gson = Gson()
        return gson.fromJson(json, MovieDetailResponse::class.java)
    }

    private fun parseMovieResponseFromJson(json: String): MovieResponse {
        val gson = Gson()
        return gson.fromJson(json, MovieResponse::class.java)
    }

    private fun parseMovieTrailersResponseFromJson(json: String): TrailersResponse {
        val gson = Gson()
        return gson.fromJson(json, TrailersResponse::class.java)
    }
}