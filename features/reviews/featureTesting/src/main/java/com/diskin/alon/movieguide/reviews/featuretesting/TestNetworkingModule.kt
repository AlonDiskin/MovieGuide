package com.diskin.alon.movieguide.reviews.featuretesting

import com.diskin.alon.movieguide.reviews.data.TheMovieDbApi
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
        return MockWebServer()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFeedlyApi(server: MockWebServer): TheMovieDbApi {
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
            .create(TheMovieDbApi::class.java)
    }
}