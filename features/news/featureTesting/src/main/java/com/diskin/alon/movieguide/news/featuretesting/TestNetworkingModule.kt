package com.diskin.alon.movieguide.news.featuretesting

import com.diskin.alon.movieguide.news.data.FeedlyApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object TestNetworkingModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideMockWebServer(): MockWebServer {
        val server =  MockWebServer()

        server.start()
        return server
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFeedlyApi(server: MockWebServer): FeedlyApi {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
            .create(FeedlyApi::class.java)
    }
}