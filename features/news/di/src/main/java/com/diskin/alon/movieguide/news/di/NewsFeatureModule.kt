package com.diskin.alon.movieguide.news.di

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.movieguide.news.appservices.HeadlineRepository
import com.diskin.alon.movieguide.news.data.HeadlineRepositoryImpl
import com.diskin.alon.movieguide.news.presentation.MoviesHeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.MoviesHeadlinesViewModel
import com.diskin.alon.movieguide.news.presentation.MoviesHeadlinesViewModelImpl
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
    }

    @Binds
    abstract fun bindHeadlineRepository(repo: HeadlineRepositoryImpl): HeadlineRepository
}