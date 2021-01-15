package com.diskin.alon.movieguide.reviews.di.moviescontainer

import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.data.SortedMoviesRequest
import com.diskin.alon.movieguide.reviews.appservices.usecase.GetSortedMoviesUseCase
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.util.MoviePagingMapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.reactivex.Observable

@Module
@InstallIn(ViewModelComponent::class)
abstract class MoviesModule {

    @ViewModelScoped
    @Binds
    abstract fun provideGetMoviesUseCase(useCase: GetSortedMoviesUseCase): UseCase<SortedMoviesRequest, Observable<PagingData<MovieDto>>>

    @Binds
    abstract fun bindMoviePagingMapper(mapper: MoviePagingMapper): Mapper<PagingData<MovieDto>, PagingData<Movie>>
}