package com.diskin.alon.movieguide.reviews.di

import com.diskin.alon.movieguide.reviews.data.remote.MOVIE_DB_API_BASE
import com.diskin.alon.movieguide.reviews.data.remote.TheMovieDbApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object ReviewsNetworkingModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideFeedlyApi(httpClient: OkHttpClient): TheMovieDbApi {
        return Retrofit.Builder()
            .baseUrl(MOVIE_DB_API_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(TheMovieDbApi::class.java)
    }
}