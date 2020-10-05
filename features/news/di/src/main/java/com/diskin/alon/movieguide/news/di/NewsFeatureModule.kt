package com.diskin.alon.movieguide.news.di

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.appservices.interfaces.HeadlineRepository
import com.diskin.alon.movieguide.news.data.implementation.ArticleRepositoryImpl
import com.diskin.alon.movieguide.news.data.implementation.HeadlineRepositoryImpl
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.controller.MoviesHeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModel
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModelImpl
import com.diskin.alon.movieguide.news.presentation.viewmodel.MoviesHeadlinesViewModel
import com.diskin.alon.movieguide.news.presentation.viewmodel.MoviesHeadlinesViewModelImpl
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
    }

    @Binds
    abstract fun bindHeadlineRepository(repo: HeadlineRepositoryImpl): HeadlineRepository

    @Binds
    abstract fun bindArticleRepository(repo: ArticleRepositoryImpl): ArticleRepository
}