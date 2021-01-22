package com.diskin.alon.movieguide.news.di.common

import com.diskin.alon.movieguide.news.data.remote.FEEDLY_BASE
import com.diskin.alon.movieguide.news.data.remote.FeedlyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsNetworkingModule {

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