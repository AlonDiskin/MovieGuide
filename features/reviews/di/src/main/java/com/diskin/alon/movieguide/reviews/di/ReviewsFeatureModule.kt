package com.diskin.alon.movieguide.reviews.di

import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.model.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.usecase.MovieDtoPagingMapper
import com.diskin.alon.movieguide.reviews.data.MovieEntityMapper
import com.diskin.alon.movieguide.reviews.data.MovieRepositoryImpl
import com.diskin.alon.movieguide.reviews.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.domain.MovieEntity
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesViewModel
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class ReviewsFeatureModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideMoviesViewModel(
            fragment: MoviesFragment,
            factory: MoviesViewModelFactory
        ): MoviesViewModel {
            return ViewModelProvider(fragment, factory)
                .get(MoviesViewModelImpl::class.java)
        }

        @JvmStatic
        @Provides
        fun provideMovieEntityMapper(): Mapper<List<MoviesResponse.MovieResponse>, List<MovieEntity>> {
            return MovieEntityMapper()
        }
    }

    @Binds
    abstract fun bindMovieRepository(repo: MovieRepositoryImpl): MovieRepository

    @Binds
    abstract fun provideMoviePagingDtoMapper(mapper: MovieDtoPagingMapper): Mapper<PagingData<MovieEntity>, PagingData<MovieDto>>
}