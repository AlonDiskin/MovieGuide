package com.diskin.alon.movieguide.di

import com.diskin.alon.movieguide.news.di.NewsNetworkingModule
import com.diskin.alon.movieguide.reviews.di.ReviewsNetworkingModule
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module(includes = [NewsNetworkingModule::class,ReviewsNetworkingModule::class])
object NetworkingModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideLogging(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC

        return logging
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
}