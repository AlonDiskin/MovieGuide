package com.diskin.alon.movieguide.news.data

import com.diskin.alon.movieguide.news.data.remote.FeedlyApi
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyArticleId
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyFeedResponse
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
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
import java.net.HttpURLConnection

/**
 * [FeedlyApi] integration test.
 */
class FeedlyApiTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Fix MockWebServer 'No password supplied for PKCS#12 KeyStore' bug on CI machine
            System.setProperty("javax.net.ssl.trustStore", "NONE")
        }
    }

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
    fun getFeedItemsFromServerAndMapResponse() {
        // Test case fixture
        val dispatcher = object : Dispatcher() {
            val feedResourcePath = "feedly_movie_news_page.json"
            val feedId = "feed/http://www.collider.com/rss.asp"
            val continuation = "176059ebed3:103a32c:951e5be4"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val feedPath = "/streams/contents"

                return when(request.requestUrl.uri().path) {
                    feedPath -> {
                        return if (
                            request.requestUrl.queryParameter("streamId") == feedId &&
                            (request.requestUrl.queryParameterNames().contains("count") ||
                                    request.requestUrl.queryParameter("continuation") == continuation)
                        ) {
                            MockResponse()
                                .setBody(getJsonFromResource(feedResourcePath))
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

        // When api client sends get request to server for existing feed items
        var testObserver = api.getFeedItems(dispatcher.feedId, 10).test()

        // Then api should map server response to expected data model
        val gson = Gson()
        val serverJson = getJsonFromResource(dispatcher.feedResourcePath)
        val expectedApiResponse = gson.fromJson(serverJson, FeedlyFeedResponse::class.java)
        testObserver.assertValue { it == expectedApiResponse }

        // When api client sends get request to server for a specific existing page
        testObserver = api.getFeedItemsPage(dispatcher.feedId, dispatcher.continuation, 10).test()

        // Then api should map server response to expected data model
        testObserver.assertValue { it == expectedApiResponse }
    }

    @Test
    fun getFeedEntryFromServerAndMapResponse() {
        // Test case fixture
        val dispatcher = object : Dispatcher() {
            val entryResourcePath = "feedly_entry.json"
            val entryId = "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_17504636bf4:2bc6fd3:951e5be4"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val entryPath = "/entries/${entryId}"

                return when(request.requestUrl.uri().path) {
                    entryPath -> {
                        MockResponse()
                            .setBody(getJsonFromResource(entryResourcePath))
                            .setResponseCode(200)
                    }

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Given remote api server is running and api client is initialized
        server.setDispatcher(dispatcher)

        // When api client sends get request to server for existing feed entry
        val testObserver = api.getEntry(dispatcher.entryId).test()

        // Then api should map server response to expected data model
        val gson = Gson()
        val serverJson = getJsonFromResource(dispatcher.entryResourcePath)
        val expectedApiResponse = gson.fromJson(serverJson, Array<FeedlyEntryResponse>::class.java).toList()
        testObserver.assertValue { it == expectedApiResponse }
    }

    @Test
    fun getFeedEntriesFromServerAndMapResponse() {
        // Test case fixture
        val dispatcher = object : Dispatcher() {
            val entriesResourcePath = "feedly_entries.json"
            val entry1Id = "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_17504636bf4:2bc6fd3:951e5be4"
            val entry2Id = "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174e66eaaf4:7efce1:58da7475"

            override fun dispatch(request: RecordedRequest): MockResponse {
                val entriesPath = "/entries/.mget"
                val requestBodyJson = JsonArray()
                val element1 = JsonObject()
                val element2 = JsonObject()

                element1.addProperty("id",entry1Id)
                element2.addProperty("id",entry2Id)

                requestBodyJson.add(element1)
                requestBodyJson.add(element2)

                return when(request.requestUrl.uri().path) {
                    entriesPath -> {
                        if (request.method == "POST" && request.body.readUtf8() == requestBodyJson.toString()) {
                            MockResponse()
                                .setBody(getJsonFromResource(entriesResourcePath))
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

        // When api client sends post request to server for existing feed entries
        val testObserver = api.getEntries(
            listOf(
                FeedlyArticleId(dispatcher.entry1Id),
                FeedlyArticleId(dispatcher.entry2Id)
            )
        )
            .test()

        val gson = Gson()
        val serverJson = getJsonFromResource(dispatcher.entriesResourcePath)
        val expectedApiResponse = gson.fromJson(serverJson, Array<FeedlyEntryResponse>::class.java).toList()
        testObserver.assertValue { it == expectedApiResponse }
    }

    @Test
    fun notEncodeIdPathWhenSendingGetEntryRequest() {
        // Given
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)

        server.enqueue(response)

        // When api is asked to get an entry
        val id = createIllegalPathId()
        api.getEntry(id).test()

        // Then api should not encode entry id path
        val expectedPath = "/entries/${id}"
        assertThat(server.takeRequest().path).isEqualTo(expectedPath)
    }

    private fun getJsonFromResource(resource: String): String {
        val topLevelClass = object : Any() {}.javaClass.enclosingClass!!
        val jsonResource = topLevelClass.classLoader!! // javaClass.classLoader
            .getResource(resource)

        return File(jsonResource.toURI()).readText()
    }
}