package com.diskin.alon.movieguide.reviews.data

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * [TheMovieDbApi] integration test class.
 */
class TheMovieDbApiTest {

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
    fun getMoviesByPopularityAndMapResponse() {
        // Text case fixture
        val dispatcher = object : Dispatcher() {
            val pageNum = 1
            val moviesResourcePath = "themoviedb_movies_sorted_by_popularity.json"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = "/${MOVIE_DB_MOVIES_PATH}?${MOVIE_DB_POP_MOVIES_PARAMS}&${MOVIE_DB_PARAM_PAGE}=${pageNum}"

                return when(request.path) {
                    path -> MockResponse()
                        .setBody(getJsonFromResource(moviesResourcePath))
                        .setResponseCode(200)

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Given 'the movie db' api server is running
        server.setDispatcher(dispatcher)

        // When api sends get request to fetch movies sorted by popularity
        val testObserver = api.getByPopularity(dispatcher.pageNum).test()

        // Then api should map server response to expected data model
        val testWebJson = getJsonFromResource(dispatcher.moviesResourcePath)
        val expectedApiResponse = parseMoviesResponseFromJson(testWebJson)
        testObserver.assertValue { it == expectedApiResponse }
    }

    @Test
    fun getMoviesByReleaseDateAndMapResponse() {
        // Text case fixture
        val dispatcher = object : Dispatcher() {
            val pageNum = 1
            val moviesResourcePath = "themoviedb_movies_sorted_by_date.json"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = "/${MOVIE_DB_MOVIES_PATH}?${MOVIE_DB_RELEASE_DATE_MOVIES_PARAMS}&${MOVIE_DB_PARAM_PAGE}=${pageNum}"

                return when(request.path) {
                    path -> MockResponse()
                        .setBody(getJsonFromResource(moviesResourcePath))
                        .setResponseCode(200)

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Given 'the movie db' api server is running
        server.setDispatcher(dispatcher)

        // When api sends get request to fetch movies sorted by release date
        val testObserver = api.getByReleaseDate(dispatcher.pageNum).test()

        // Then api should map server response to expected data model
        val testWebJson = getJsonFromResource(dispatcher.moviesResourcePath)
        val expectedApiResponse = parseMoviesResponseFromJson(testWebJson)
        testObserver.assertValue { it == expectedApiResponse }
    }

    @Test
    fun getMoviesByRatingAndMapResponse() {
        // Text case fixture
        val dispatcher = object : Dispatcher() {
            val pageNum = 1
            val moviesResourcePath = "themoviedb_movies_sorted_by_rating.json"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = "/${MOVIE_DB_MOVIES_PATH}?${MOVIE_DB_RATING_MOVIES_PARAMS}&${MOVIE_DB_PARAM_PAGE}=${pageNum}"

                return when(request.path) {
                    path -> MockResponse()
                        .setBody(getJsonFromResource(moviesResourcePath))
                        .setResponseCode(200)

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Given 'the movie db' api server is running
        server.setDispatcher(dispatcher)

        // When api sends get request to fetch movies sorted by rating
        val testObserver = api.getByRating(dispatcher.pageNum).test()

        // Then api should map server response to expected data model
        val testWebJson = getJsonFromResource(dispatcher.moviesResourcePath)
        val expectedApiResponse = parseMoviesResponseFromJson(testWebJson)
        testObserver.assertValue { it == expectedApiResponse }
    }

    private fun getJsonFromResource(resource: String): String {
        val topLevelClass = object : Any() {}.javaClass.enclosingClass!!
        val jsonResource = topLevelClass.classLoader!! // javaClass.classLoader
            .getResource(resource)

        return File(jsonResource.toURI()).readText()
    }

    private fun parseMoviesResponseFromJson(json: String): MoviesResponse {
        val gson = Gson()
        return gson.fromJson(json,MoviesResponse::class.java)
    }
}