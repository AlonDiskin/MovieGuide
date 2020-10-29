package com.diskin.alon.movieguide.news.di

import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.appservices.interfaces.HeadlineRepository
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.appservices.model.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.usecase.ArticleDtoMapper
import com.diskin.alon.movieguide.news.appservices.usecase.HeadlinesDtoPagingMapper
import com.diskin.alon.movieguide.news.data.implementation.ArticleRepositoryImpl
import com.diskin.alon.movieguide.news.data.implementation.HeadlineRepositoryImpl
import com.diskin.alon.movieguide.news.data.remote.ApiArticleMapper
import com.diskin.alon.movieguide.news.data.remote.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.data.remote.RemoteArticleStore
import com.diskin.alon.movieguide.news.data.remote.RemoteNewsStore
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.controller.MoviesHeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.viewmodel.*
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class NewsFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideMoviesHeadlinesViewModel(
            fragment: MoviesHeadlinesFragment,
            factory: MoviesHeadlinesViewModelProvider
        ): MoviesHeadlinesViewModel {
            return ViewModelProvider(fragment,factory)
                .get(MoviesHeadlinesViewModelImpl::class.java)
        }

        @JvmStatic
        @Provides
        fun provideArticleViewModel(
            activity: ArticleActivity,
            factory: ArticleViewModelFactory
        ): ArticleViewModel {
            return ViewModelProvider(activity,factory)
                .get(ArticleViewModelImpl::class.java)
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
    abstract fun bindHeadlineRepository(repo: HeadlineRepositoryImpl): HeadlineRepository

    @Binds
    abstract fun bindArticleRepository(repo: ArticleRepositoryImpl): ArticleRepository

    @Binds
    abstract fun bindArticleDtoMapper(mapper: ArticleDtoMapper): Mapper<Result<ArticleEntity>, Result<ArticleDto>>

    @Binds
    abstract fun bindHeadlinesDtoPagingMapper(mapper: HeadlinesDtoPagingMapper): Mapper<PagingData<HeadlineEntity>, PagingData<HeadlineDto>>
}