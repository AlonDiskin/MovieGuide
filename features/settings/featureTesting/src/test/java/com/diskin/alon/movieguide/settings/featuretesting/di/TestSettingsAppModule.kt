package com.diskin.alon.movieguide.settings.featuretesting.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestSettingsAppModule {

    @Provides
    @Singleton
    fun provideEventBus(): EventBus {
        return mockk<EventBus>()
    }
}