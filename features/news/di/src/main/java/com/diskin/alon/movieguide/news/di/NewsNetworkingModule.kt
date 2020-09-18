package com.diskin.alon.movieguide.news.di

import com.diskin.alon.movieguide.news.data.FEEDLY_BASE
import com.diskin.alon.movieguide.news.data.FeedlyApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object NewsNetworkingModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideFeedlyApi(httpClient: OkHttpClient): FeedlyApi {
        return Retrofit.Builder()
            .baseUrl(FEEDLY_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(FeedlyApi::class.java)
    }
}