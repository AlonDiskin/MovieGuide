package com.diskin.alon.movieguide.news.di.feature

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.appservices.interfaces.HeadlineRepository
import com.diskin.alon.movieguide.news.data.implementation.ArticleRepositoryImpl
import com.diskin.alon.movieguide.news.data.implementation.HeadlineRepositoryImpl
import com.diskin.alon.movieguide.news.data.local.Bookmark
import com.diskin.alon.movieguide.news.data.local.BookmarkedHeadlineMapper
import com.diskin.alon.movieguide.news.data.local.BookmarkedHeadlinesStore
import com.diskin.alon.movieguide.news.data.local.BookmarkedHeadlinesStoreImpl
import com.diskin.alon.movieguide.news.data.remote.*
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class NewsDataModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideLocalHeadlinesMapper(): Mapper<List<Bookmark>,Result<List<HeadlineEntity>>> {
            return BookmarkedHeadlineMapper()
        }

        @JvmStatic
        @Provides
        fun provideApiArticleMapper(): Mapper<List<FeedlyEntryResponse>, Result<ArticleEntity>> {
            return ApiArticleMapper()
        }
    }

    @Binds
    abstract fun bindRemoteArticleStore(store: RemoteNewsStore): RemoteArticleStore

    @Binds
    abstract fun bindArticleRepository(repo: ArticleRepositoryImpl): ArticleRepository

    @Binds
    abstract fun bindHeadlineRepository(repo: HeadlineRepositoryImpl): HeadlineRepository

    @Binds
    abstract fun bindRemoteHeadlinesStore(store: RemoteHeadlinesStoreImpl): RemoteHeadlinesStore

    @Binds
    abstract fun bindLocalHeadlinesStore(store: BookmarkedHeadlinesStoreImpl): BookmarkedHeadlinesStore
}