package com.diskin.alon.movieguide.di

import com.diskin.alon.movieguide.news.data.remote.FeedlyApi
import com.diskin.alon.movieguide.reviews.data.remote.TheMovieDbApi
import com.diskin.alon.movieguide.util.NetworkUtil
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object TestNetworkingModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFeedlyApi(httpClient: OkHttpClient): FeedlyApi {
        return Retrofit.Builder()
            .baseUrl(NetworkUtil.url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(FeedlyApi::class.java)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideTheMovieDbApi(httpClient: OkHttpClient): TheMovieDbApi {
        return Retrofit.Builder()
            .baseUrl(NetworkUtil.url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(TheMovieDbApi::class.java)
    }
}