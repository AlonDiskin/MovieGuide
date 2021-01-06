package com.diskin.alon.movieguide.reviews.di.moviescontainer

import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.usecase.MovieDtoPagingMapper
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.util.MoviePagingMapper
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesViewModel
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class MoviesModule {

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
    }

    @Binds
    abstract fun bindMoviePagingMapper(mapper: MoviePagingMapper): Mapper<PagingData<MovieDto>, PagingData<Movie>>

    @Binds
    abstract fun provideMoviePagingDtoMapper(mapper: MovieDtoPagingMapper): Mapper<PagingData<MovieEntity>, PagingData<MovieDto>>
}