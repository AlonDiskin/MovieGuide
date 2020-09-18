package com.diskin.alon.movieguide.news.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

/**
 * [FeedlyApi] integration test.
 */
class FeedlyApiTest {

    private lateinit var api: FeedlyApi
    private val server = MockWebServer()

    @Before
    fun setUp() {
        // Start mocked web server
        server.start()

        // Init feedly api client
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
            .create(FeedlyApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun mapServerResponseWhenFeedGetRequestSuccess() {
        // Given
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(getFeedlyFeedJsonResponse())

        server.enqueue(response)

        // When
        val testObserver = api.getFeedItems(MOVIES_NEWS_FEED,3).test()

        // Then
        testObserver.assertValue(getMappedFeedlyResponse())
    }

    @Test
    fun mapServerResponseGetFeedLastPageSuccess() {
        // Given
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(getFeedlyFeedLastPageJsonResponse())

        server.enqueue(response)

        // When
        val testObserver = api.getFeedItems(MOVIES_NEWS_FEED,3).test()

        // Then
        testObserver.assertValue(getMappedFeedlyLastPageResponse())
    }
}