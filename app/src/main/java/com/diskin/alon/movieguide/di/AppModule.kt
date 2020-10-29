package com.diskin.alon.movieguide.di

import android.app.Application
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideResources(app: Application): Resources {
        return app.resources
    }
}