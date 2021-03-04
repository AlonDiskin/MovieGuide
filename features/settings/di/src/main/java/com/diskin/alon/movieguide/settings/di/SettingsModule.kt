package com.diskin.alon.movieguide.settings.di

import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ModelDispatcher
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.settings.appservices.interfaces.AppThemeManager
import com.diskin.alon.movieguide.settings.appservices.interfaces.NewsUpdateNotificationManager
import com.diskin.alon.movieguide.settings.appservices.usecase.ConfigAppThemeUseCase
import com.diskin.alon.movieguide.settings.appservices.usecase.ConfigNewsNotificationUseCase
import com.diskin.alon.movieguide.settings.infrastructure.AppThemeManagerImpl
import com.diskin.alon.movieguide.settings.infrastructure.NewsUpdateNotificationManagerImpl
import com.diskin.alon.movieguide.settings.presentation.data.AppThemeModelRequest
import com.diskin.alon.movieguide.settings.presentation.data.ConfigNewsNotificationModelRequest
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class SettingsModule {

    companion object {

        @Provides
        fun provideModel(
            configAppThemeUseCase: ConfigAppThemeUseCase,
            configNewsNotificationUseCase: ConfigNewsNotificationUseCase
        ): Model {
            val map = HashMap<Class<out ModelRequest<*, *>>,Pair<UseCase<*, *>, Mapper<*, *>?>>()

            map[ConfigNewsNotificationModelRequest::class.java] = Pair(configNewsNotificationUseCase,null)
            map[AppThemeModelRequest::class.java] = Pair(configAppThemeUseCase,null)

            return ModelDispatcher(map)
        }
    }

    @Binds
    abstract fun bindThemeManager(manager: AppThemeManagerImpl): AppThemeManager

    @Binds
    abstract fun bindNewsNotificationManager(manager: NewsUpdateNotificationManagerImpl): NewsUpdateNotificationManager
}