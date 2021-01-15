package com.diskin.alon.movieguide.news.di.common

import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.data.implementation.ArticleRepositoryImpl
import com.diskin.alon.movieguide.news.data.local.BookmarkStore
import com.diskin.alon.movieguide.news.data.local.BookmarkStoreImpl
import com.diskin.alon.movieguide.news.data.local.StorageErrorHandler
import com.diskin.alon.movieguide.news.data.local.StorageErrorHandlerImpl
import com.diskin.alon.movieguide.news.data.remote.RemoteArticleStore
import com.diskin.alon.movieguide.news.data.remote.RemoteArticleStoreImpl
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.data.remote.util.ApiArticleMapper
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.util.HeadlineMapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NewsDataModule {

    @Singleton
    @Binds
    abstract fun bindArticleRepository(repo: ArticleRepositoryImpl): ArticleRepository

    @Singleton
    @Binds
    abstract fun bindsRemoteArticleStore(store: RemoteArticleStoreImpl): RemoteArticleStore

    @Singleton
    @Binds
    abstract fun bindsBookmarkStore(store: BookmarkStoreImpl): BookmarkStore

    @Singleton
    @Binds
    abstract fun bindsApiArticleMapper(mapper: ApiArticleMapper): Mapper<FeedlyEntryResponse, ArticleEntity>

    @Singleton
    @Binds
    abstract fun bindsStorageErrorHandler(handler: StorageErrorHandlerImpl): StorageErrorHandler

    @Singleton
    @Binds
    abstract fun bindsHeadlineMapper(mapper: HeadlineMapper): Mapper<HeadlineDto, Headline>
}