package com.diskin.alon.movieguide.news.di.common

import com.diskin.alon.movieguide.common.util.Mapper
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
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class NewsDataModule {

    @Binds
    abstract fun bindArticleRepository(repo: ArticleRepositoryImpl): ArticleRepository

    @Binds
    abstract fun bindsRemoteArticleStore(store: RemoteArticleStoreImpl): RemoteArticleStore

    @Binds
    abstract fun bindsBookmarkStore(store: BookmarkStoreImpl): BookmarkStore

    @Binds
    abstract fun bindsApiArticleMapper(mapper: ApiArticleMapper): Mapper<FeedlyEntryResponse, ArticleEntity>

    @Binds
    abstract fun bindsStorageErrorHandler(handler: StorageErrorHandlerImpl): StorageErrorHandler
}